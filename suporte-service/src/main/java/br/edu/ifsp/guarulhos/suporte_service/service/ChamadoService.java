package br.edu.ifsp.guarulhos.suporte_service.service;

import br.edu.ifsp.guarulhos.suporte_service.dto.request.AbrirChamadoRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.request.AlterarStatusRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.request.ResponderChamadoRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.response.ChamadoResponse;
import br.edu.ifsp.guarulhos.suporte_service.dto.response.ChamadoResumoResponse;
import br.edu.ifsp.guarulhos.suporte_service.dto.response.RespostaResponse;
import br.edu.ifsp.guarulhos.suporte_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.suporte_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.suporte_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.suporte_service.model.Chamado;
import br.edu.ifsp.guarulhos.suporte_service.model.RespostaChamado;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import br.edu.ifsp.guarulhos.suporte_service.repository.ChamadoRepository;
import br.edu.ifsp.guarulhos.suporte_service.repository.RespostaChamadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChamadoService {

    private static final String PERFIL_MODERADOR = "MODERADOR";
    private static final long HORAS_PARA_URGENTE = 48;

    private final ChamadoRepository chamadoRepository;
    private final RespostaChamadoRepository respostaChamadoRepository;

    @Transactional
    public ChamadoResponse abrirChamado(AbrirChamadoRequest request, Long usuarioId){
        Chamado chamado = Chamado.builder()
                .usuarioId(usuarioId)
                .tipoProblema(request.getTipoProblema())
                .descricao(request.getDescricao())
                .capturaTelaUrl(request.getCapturaTelaUrl())
                .status(StatusChamado.ABERTO)
                .build();
        chamado = chamadoRepository.save(chamado);

        chamado.setProtocolo(gerarProtocolo(chamado.getId()));
        chamado = chamadoRepository.save(chamado);

        return toResponse(chamado, List.of());
    }

    public List<ChamadoResumoResponse> listarMeusChamados(Long usuarioId){
        return chamadoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId).stream()
                .map(this::toResumo)
                .toList();
    }

    public ChamadoResponse buscarPorProtocolo(String protocolo, Long usuarioId, String perfil){
        Chamado chamado = chamadoRepository.findByProtocolo(protocolo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado"));

        boolean ehDono = chamado.getUsuarioId().equals(usuarioId);
        boolean ehModerador = PERFIL_MODERADOR.equals(perfil);
        if (!ehDono && !ehModerador){
            throw new AcessoNegadoException("Você não tem permissão para visualizar este chamado");
        }

        List<RespostaChamado> respostas = respostaChamadoRepository
                .findByChamadoIdOrderByCriadoEmAsc(chamado.getId());
        return toResponse(chamado, respostas);
    }

    public List<ChamadoResumoResponse> listarPainel(StatusChamado statusFiltro, String perfil){
        validarModerador(perfil);

        List<Chamado> chamados = statusFiltro != null
                ? chamadoRepository.findByStatusOrderByCriadoEmDesc(statusFiltro)
                : chamadoRepository.findAllByOrderByCriadoEmDesc();

        return chamados.stream().map(this::toResumo).toList();
    }

    @Transactional
    public ChamadoResponse responder(Long chamadoId, ResponderChamadoRequest request, Long moderadorId, String perfil){
        validarModerador(perfil);
        Chamado chamado = buscarPorId(chamadoId);

        if (chamado.getStatus() == StatusChamado.ENCERRADO){
            throw new RegraNegocioException("Não é possível responder um chamado encerrado");
        }

        RespostaChamado resposta = RespostaChamado.builder()
                .chamado(chamado)
                .moderadorId(moderadorId)
                .mensagem(request.getMensagem())
                .build();
        respostaChamadoRepository.save(resposta);

        chamado.setAtualizadoEm(LocalDateTime.now());
        chamadoRepository.save(chamado);

        List<RespostaChamado> respostas = respostaChamadoRepository
                .findByChamadoIdOrderByCriadoEmAsc(chamado.getId());
        return toResponse(chamado, respostas);
    }

    @Transactional
    public ChamadoResponse alterarStatus(Long chamadoId, AlterarStatusRequest request, String perfil){
        validarModerador(perfil);
        Chamado chamado = buscarPorId(chamadoId);

        if (chamado.getStatus() == StatusChamado.ENCERRADO){
            throw new RegraNegocioException("Não é possível alterar o status de um chamado encerrado");
        }

        chamado.setStatus(request.getStatus());
        chamado.setAtualizadoEm(LocalDateTime.now());
        chamado = chamadoRepository.save(chamado);

        List<RespostaChamado> respostas = respostaChamadoRepository
                .findByChamadoIdOrderByCriadoEmAsc(chamado.getId());
        return toResponse(chamado, respostas);
    }

    private Chamado buscarPorId(Long chamadoId){
        return chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado"));
    }

    private void validarModerador(String perfil){
        if (!PERFIL_MODERADOR.equals(perfil)){
            throw new AcessoNegadoException("Apenas moderadores podem acessar este recurso");
        }
    }

    private String gerarProtocolo(Long id){
        int ano = LocalDateTime.now().getYear();
        return "SUP-%d-%06d".formatted(ano, id);
    }

    private boolean isUrgente(Chamado chamado){
        boolean emAberto = chamado.getStatus() == StatusChamado.ABERTO
                || chamado.getStatus() == StatusChamado.EM_ATENDIMENTO;
        return emAberto && chamado.getAtualizadoEm()
                .isBefore(LocalDateTime.now().minusHours(HORAS_PARA_URGENTE));
    }

    private ChamadoResumoResponse toResumo(Chamado chamado){
        return ChamadoResumoResponse.builder()
                .id(chamado.getId())
                .protocolo(chamado.getProtocolo())
                .usuarioId(chamado.getUsuarioId())
                .tipoProblema(chamado.getTipoProblema())
                .status(chamado.getStatus())
                .urgente(isUrgente(chamado))
                .criadoEm(chamado.getCriadoEm())
                .atualizadoEm(chamado.getAtualizadoEm())
                .build();
    }

    private ChamadoResponse toResponse(Chamado chamado, List<RespostaChamado> respostas){
        return ChamadoResponse.builder()
                .id(chamado.getId())
                .protocolo(chamado.getProtocolo())
                .usuarioId(chamado.getUsuarioId())
                .tipoProblema(chamado.getTipoProblema())
                .descricao(chamado.getDescricao())
                .capturaTelaUrl(chamado.getCapturaTelaUrl())
                .status(chamado.getStatus())
                .criadoEm(chamado.getCriadoEm())
                .atualizadoEm(chamado.getAtualizadoEm())
                .respostas(respostas.stream()
                        .map(r -> RespostaResponse.builder()
                                .moderadorId(r.getModeradorId())
                                .mensagem(r.getMensagem())
                                .criadoEm(r.getCriadoEm())
                                .build())
                        .toList())
                .build();
    }
}
