package br.edu.ifsp.guarulhos.gamification_service.service;

import br.edu.ifsp.guarulhos.gamification_service.dto.request.EventoPontosRequest;
import br.edu.ifsp.guarulhos.gamification_service.dto.response.ConquistaResponse;
import br.edu.ifsp.guarulhos.gamification_service.dto.response.PontosEventoResponse;
import br.edu.ifsp.guarulhos.gamification_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.gamification_service.model.Conquista;
import br.edu.ifsp.guarulhos.gamification_service.model.PontosEvento;
import br.edu.ifsp.guarulhos.gamification_service.model.UsuarioConquista;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.TipoEvento;
import br.edu.ifsp.guarulhos.gamification_service.repository.ConquistaRepository;
import br.edu.ifsp.guarulhos.gamification_service.repository.PontosEventoRepository;
import br.edu.ifsp.guarulhos.gamification_service.repository.UsuarioConquistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de pontuação (US-12): valora os eventos recebidos dos demais serviços, garante
 * idempotência (o mesmo fato não pontua duas vezes) e dispara o desbloqueio de conquistas.
 */
@Service
@RequiredArgsConstructor
public class PontuacaoService {

    // Tabela de pontos definida na US-12.
    private static final int PONTOS_TOPICO = 5;
    private static final int PONTOS_COMENTARIO = 3;
    private static final int PONTOS_LIKE = 2;
    private static final int PONTOS_EXERCICIO_FACIL = 10;
    private static final int PONTOS_EXERCICIO_MEDIO = 20;
    private static final int PONTOS_EXERCICIO_DIFICIL = 40;

    private final PontosEventoRepository pontosEventoRepository;
    private final ConquistaRepository conquistaRepository;
    private final UsuarioConquistaRepository usuarioConquistaRepository;

    @Transactional
    public void registrarEvento(EventoPontosRequest request){
        // Idempotência: se o mesmo fato já pontuou, ignora silenciosamente.
        boolean jaPontuado = pontosEventoRepository
                .existsByTipoAndReferenciaIdAndUsuarioId(request.getTipo(), request.getReferenciaId(), request.getUsuarioId());
        if (jaPontuado){
            return;
        }

        int pontos = calcularPontos(request.getTipo(), request.getDificuldade());

        PontosEvento evento = PontosEvento.builder()
                .tipo(request.getTipo())
                .usuarioId(request.getUsuarioId())
                .referenciaId(request.getReferenciaId())
                .pontos(pontos)
                .build();
        pontosEventoRepository.save(evento);

        avaliarConquistas(request.getUsuarioId());
    }

    public List<PontosEventoResponse> extrato(Long usuarioId){
        return pontosEventoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId).stream()
                .map(evento -> PontosEventoResponse.builder()
                        .tipo(evento.getTipo())
                        .referenciaId(evento.getReferenciaId())
                        .pontos(evento.getPontos())
                        .criadoEm(evento.getCriadoEm())
                        .build())
                .toList();
    }

    public List<ConquistaResponse> conquistasDoUsuario(Long usuarioId){
        return usuarioConquistaRepository.findByUsuarioId(usuarioId).stream()
                .map(uc -> {
                    Conquista conquista = conquistaRepository.findById(uc.getConquistaId()).orElseThrow();
                    return ConquistaResponse.builder()
                            .id(conquista.getId())
                            .nome(conquista.getNome())
                            .descricao(conquista.getDescricao())
                            .desbloqueadaEm(uc.getDesbloqueadaEm())
                            .build();
                })
                .toList();
    }

    private int calcularPontos(TipoEvento tipo, Dificuldade dificuldade){
        return switch (tipo){
            case TOPICO_CRIADO -> PONTOS_TOPICO;
            case COMENTARIO -> PONTOS_COMENTARIO;
            case LIKE_RECEBIDO -> PONTOS_LIKE;
            case EXERCICIO_RESOLVIDO -> pontosPorDificuldade(dificuldade);
        };
    }

    private int pontosPorDificuldade(Dificuldade dificuldade){
        if (dificuldade == null){
            throw new RegraNegocioException("Dificuldade é obrigatória para eventos de exercício resolvido");
        }
        return switch (dificuldade){
            case FACIL -> PONTOS_EXERCICIO_FACIL;
            case MEDIO -> PONTOS_EXERCICIO_MEDIO;
            case DIFICIL -> PONTOS_EXERCICIO_DIFICIL;
        };
    }

    /**
     * Avalia e desbloqueia conquistas que o usuário tenha atingido após o novo evento.
     * TODO: implementar as regras concretas (ex.: PRIMEIRO_TOPICO, 10_EXERCICIOS) consultando
     * os totais do usuário e gravando em UsuarioConquista quando o critério for satisfeito.
     */
    private void avaliarConquistas(Long usuarioId){
        // Esqueleto: regras de desbloqueio a definir junto com o catálogo de badges.
    }

    /** Marca uma conquista como desbloqueada para o usuário, evitando duplicidade. */
    @SuppressWarnings("unused")
    private void desbloquear(Long usuarioId, Conquista conquista){
        if (usuarioConquistaRepository.existsByUsuarioIdAndConquistaId(usuarioId, conquista.getId())){
            return;
        }
        usuarioConquistaRepository.save(UsuarioConquista.builder()
                .usuarioId(usuarioId)
                .conquistaId(conquista.getId())
                .build());
    }
}
