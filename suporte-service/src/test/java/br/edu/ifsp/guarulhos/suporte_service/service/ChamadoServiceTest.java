package br.edu.ifsp.guarulhos.suporte_service.service;

import br.edu.ifsp.guarulhos.suporte_service.dto.request.AbrirChamadoRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.request.AlterarStatusRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.request.ResponderChamadoRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.response.ChamadoResponse;
import br.edu.ifsp.guarulhos.suporte_service.dto.response.ChamadoResumoResponse;
import br.edu.ifsp.guarulhos.suporte_service.exception.AcessoNegadoException;
import br.edu.ifsp.guarulhos.suporte_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.suporte_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.suporte_service.model.Chamado;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.TipoProblema;
import br.edu.ifsp.guarulhos.suporte_service.repository.ChamadoRepository;
import br.edu.ifsp.guarulhos.suporte_service.repository.RespostaChamadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;
    @Mock
    private RespostaChamadoRepository respostaChamadoRepository;
    @InjectMocks
    private ChamadoService chamadoService;

    @Test
    void abrirChamado_geraProtocoloEPersisteComStatusAberto(){
        AbrirChamadoRequest request = new AbrirChamadoRequest();
        request.setTipoProblema(TipoProblema.BUG);
        request.setDescricao("A plataforma não carrega o catálogo de exercícios.");

        when(chamadoRepository.save(any(Chamado.class))).thenAnswer(invocation -> {
            Chamado c = invocation.getArgument(0);
            if (c.getId() == null){
                c.setId(123L);
            }
            return c;
        });

        ChamadoResponse response = chamadoService.abrirChamado(request, 1L);

        assertThat(response.getStatus()).isEqualTo(StatusChamado.ABERTO);
        assertThat(response.getProtocolo()).contains("000123");
        verify(chamadoRepository, times(2)).save(any(Chamado.class));
    }

    @Test
    void responder_porNaoModerador_lancaAcessoNegado(){
        ResponderChamadoRequest request = new ResponderChamadoRequest();
        request.setMensagem("Estamos verificando o problema.");

        assertThatThrownBy(() -> chamadoService.responder(1L, request, 2L, "ESTUDANTE"))
                .isInstanceOf(AcessoNegadoException.class);

        verify(chamadoRepository, never()).findById(any());
    }

    @Test
    void responder_chamadoEncerrado_lancaRegraNegocio(){
        Chamado chamado = Chamado.builder().id(1L).status(StatusChamado.ENCERRADO)
                .usuarioId(1L).criadoEm(LocalDateTime.now()).atualizadoEm(LocalDateTime.now()).build();
        when(chamadoRepository.findById(1L)).thenReturn(Optional.of(chamado));

        ResponderChamadoRequest request = new ResponderChamadoRequest();
        request.setMensagem("Mensagem qualquer");

        assertThatThrownBy(() -> chamadoService.responder(1L, request, 2L, "MODERADOR"))
                .isInstanceOf(RegraNegocioException.class);

        verify(respostaChamadoRepository, never()).save(any());
    }

    @Test
    void alterarStatus_porModerador_funciona(){
        Chamado chamado = Chamado.builder().id(1L).status(StatusChamado.ABERTO)
                .usuarioId(1L).criadoEm(LocalDateTime.now()).atualizadoEm(LocalDateTime.now()).build();
        when(chamadoRepository.findById(1L)).thenReturn(Optional.of(chamado));
        when(chamadoRepository.save(any(Chamado.class))).thenAnswer(inv -> inv.getArgument(0));
        when(respostaChamadoRepository.findByChamadoIdOrderByCriadoEmAsc(1L)).thenReturn(List.of());

        AlterarStatusRequest request = new AlterarStatusRequest();
        request.setStatus(StatusChamado.EM_ATENDIMENTO);

        ChamadoResponse response = chamadoService.alterarStatus(1L, request, "MODERADOR");

        assertThat(response.getStatus()).isEqualTo(StatusChamado.EM_ATENDIMENTO);
    }

    @Test
    void alterarStatus_porNaoModerador_lancaAcessoNegado(){
        AlterarStatusRequest request = new AlterarStatusRequest();
        request.setStatus(StatusChamado.RESOLVIDO);

        assertThatThrownBy(() -> chamadoService.alterarStatus(1L, request, "ESTUDANTE"))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void painel_marcaUrgenteQuandoSemInteracaoHa48h(){
        Chamado antigo = Chamado.builder().id(1L).protocolo("SUP-2026-000001").usuarioId(1L)
                .tipoProblema(TipoProblema.BUG).status(StatusChamado.ABERTO)
                .criadoEm(LocalDateTime.now().minusDays(3))
                .atualizadoEm(LocalDateTime.now().minusHours(50))
                .build();
        when(chamadoRepository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(antigo));

        List<ChamadoResumoResponse> painel = chamadoService.listarPainel(null, "MODERADOR");

        assertThat(painel).hasSize(1);
        assertThat(painel.get(0).isUrgente()).isTrue();
    }

    @Test
    void painel_naoMarcaUrgenteQuandoRecente(){
        Chamado recente = Chamado.builder().id(2L).protocolo("SUP-2026-000002").usuarioId(1L)
                .tipoProblema(TipoProblema.DUVIDA).status(StatusChamado.ABERTO)
                .criadoEm(LocalDateTime.now().minusHours(2))
                .atualizadoEm(LocalDateTime.now().minusHours(2))
                .build();
        when(chamadoRepository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(recente));

        List<ChamadoResumoResponse> painel = chamadoService.listarPainel(null, "MODERADOR");

        assertThat(painel.get(0).isUrgente()).isFalse();
    }

    @Test
    void painel_filtraPorStatus_chamaRepositorioComStatus(){
        when(chamadoRepository.findByStatusOrderByCriadoEmDesc(StatusChamado.RESOLVIDO)).thenReturn(List.of());

        chamadoService.listarPainel(StatusChamado.RESOLVIDO, "MODERADOR");

        verify(chamadoRepository).findByStatusOrderByCriadoEmDesc(StatusChamado.RESOLVIDO);
        verify(chamadoRepository, never()).findAllByOrderByCriadoEmDesc();
    }

    @Test
    void buscarPorProtocolo_naoDonoNemModerador_lancaAcessoNegado(){
        Chamado chamado = Chamado.builder().id(1L).protocolo("SUP-2026-000001").usuarioId(1L)
                .status(StatusChamado.ABERTO).criadoEm(LocalDateTime.now()).atualizadoEm(LocalDateTime.now()).build();
        when(chamadoRepository.findByProtocolo("SUP-2026-000001")).thenReturn(Optional.of(chamado));

        assertThatThrownBy(() -> chamadoService.buscarPorProtocolo("SUP-2026-000001", 99L, "ESTUDANTE"))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void buscarPorProtocolo_dono_retornaChamado(){
        Chamado chamado = Chamado.builder().id(1L).protocolo("SUP-2026-000001").usuarioId(1L)
                .status(StatusChamado.ABERTO).criadoEm(LocalDateTime.now()).atualizadoEm(LocalDateTime.now()).build();
        when(chamadoRepository.findByProtocolo("SUP-2026-000001")).thenReturn(Optional.of(chamado));
        when(respostaChamadoRepository.findByChamadoIdOrderByCriadoEmAsc(1L)).thenReturn(List.of());

        ChamadoResponse response = chamadoService.buscarPorProtocolo("SUP-2026-000001", 1L, "ESTUDANTE");

        assertThat(response.getProtocolo()).isEqualTo("SUP-2026-000001");
    }

    @Test
    void buscarPorProtocolo_naoEncontrado_lancaRecursoNaoEncontrado(){
        when(chamadoRepository.findByProtocolo("INVALIDO")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chamadoService.buscarPorProtocolo("INVALIDO", 1L, "ESTUDANTE"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
