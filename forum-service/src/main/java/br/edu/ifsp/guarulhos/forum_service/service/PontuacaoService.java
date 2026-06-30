package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.RankingItemResponse;
import br.edu.ifsp.guarulhos.forum_service.model.Pontuacao;
import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoPontuacao;
import br.edu.ifsp.guarulhos.forum_service.repository.PontuacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PontuacaoService {

    private static final int PONTOS_CRIAR_TOPICO = 5;
    private static final int PONTOS_COMENTAR = 3;
    private static final int PONTOS_RECEBER_LIKE = 2;

    private final PontuacaoRepository pontuacaoRepository;

    public void registrarTopico(Long autorId, Long topicoId) {
        pontuacaoRepository.save(Pontuacao.builder()
                .usuarioId(autorId)
                .pontos(PONTOS_CRIAR_TOPICO)
                .tipo(TipoPontuacao.CRIAR_TOPICO)
                .referenciaId(topicoId)
                .build());
    }

    public void registrarComentario(Long autorId, Long comentarioId) {
        pontuacaoRepository.save(Pontuacao.builder()
                .usuarioId(autorId)
                .pontos(PONTOS_COMENTAR)
                .tipo(TipoPontuacao.COMENTAR)
                .referenciaId(comentarioId)
                .build());
    }

    public void registrarLike(Long autorConteudo, Long referenciaId, Long curtidorId) {
        boolean jaRegistrado = pontuacaoRepository
                .findByTipoAndReferenciaIdAndCurtidorId(TipoPontuacao.RECEBER_LIKE, referenciaId, curtidorId)
                .isPresent();
        if (!jaRegistrado) {
            pontuacaoRepository.save(Pontuacao.builder()
                    .usuarioId(autorConteudo)
                    .pontos(PONTOS_RECEBER_LIKE)
                    .tipo(TipoPontuacao.RECEBER_LIKE)
                    .referenciaId(referenciaId)
                    .curtidorId(curtidorId)
                    .build());
        }
    }

    public void removerPontoLike(Long autorConteudo, Long referenciaId, Long curtidorId) {
        pontuacaoRepository
                .findByTipoAndReferenciaIdAndCurtidorId(TipoPontuacao.RECEBER_LIKE, referenciaId, curtidorId)
                .ifPresent(pontuacaoRepository::delete);
    }

    public long totalPontos(Long usuarioId) {
        return pontuacaoRepository.sumPontosByUsuarioId(usuarioId);
    }

    public List<RankingItemResponse> ranking(int limite) {
        List<Object[]> raw = pontuacaoRepository.findRanking(PageRequest.of(0, limite));
        List<RankingItemResponse> resultado = new ArrayList<>();
        for (int i = 0; i < raw.size(); i++) {
            Object[] row = raw.get(i);
            resultado.add(new RankingItemResponse(
                    i + 1,
                    (Long) row[0],
                    ((Number) row[1]).longValue()
            ));
        }
        return resultado;
    }
}
