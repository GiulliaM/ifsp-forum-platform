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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class PontuacaoServiceTest {

    @Mock
    private PontosEventoRepository pontosEventoRepository;
    @Mock
    private ConquistaRepository conquistaRepository;
    @Mock
    private UsuarioConquistaRepository usuarioConquistaRepository;
    @InjectMocks
    private PontuacaoService pontuacaoService;

    @Test
    void registrarEvento_topicoCriado_salvaEventoCom5Pontos() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.TOPICO_CRIADO);
        request.setUsuarioId(1L);
        request.setReferenciaId(10L);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.TOPICO_CRIADO, 10L, 1L)).thenReturn(false);
        when(conquistaRepository.findByCriterio(any())).thenReturn(Optional.empty());

        pontuacaoService.registrarEvento(request);

        ArgumentCaptor<PontosEvento> captor = ArgumentCaptor.forClass(PontosEvento.class);
        verify(pontosEventoRepository).save(captor.capture());
        assertThat(captor.getValue().getPontos()).isEqualTo(5);
        assertThat(captor.getValue().getTipo()).isEqualTo(TipoEvento.TOPICO_CRIADO);
    }

    @Test
    void registrarEvento_comentario_salvaEventoCom3Pontos() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.COMENTARIO);
        request.setUsuarioId(1L);
        request.setReferenciaId(20L);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.COMENTARIO, 20L, 1L)).thenReturn(false);
        when(conquistaRepository.findByCriterio(any())).thenReturn(Optional.empty());

        pontuacaoService.registrarEvento(request);

        ArgumentCaptor<PontosEvento> captor = ArgumentCaptor.forClass(PontosEvento.class);
        verify(pontosEventoRepository).save(captor.capture());
        assertThat(captor.getValue().getPontos()).isEqualTo(3);
    }

    @Test
    void registrarEvento_exercicioFacil_salvaEventoCom10Pontos() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.EXERCICIO_RESOLVIDO);
        request.setUsuarioId(1L);
        request.setReferenciaId(5L);
        request.setDificuldade(Dificuldade.FACIL);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.EXERCICIO_RESOLVIDO, 5L, 1L)).thenReturn(false);
        when(conquistaRepository.findByCriterio(any())).thenReturn(Optional.empty());

        pontuacaoService.registrarEvento(request);

        ArgumentCaptor<PontosEvento> captor = ArgumentCaptor.forClass(PontosEvento.class);
        verify(pontosEventoRepository).save(captor.capture());
        assertThat(captor.getValue().getPontos()).isEqualTo(10);
    }

    @Test
    void registrarEvento_exercicioMedio_salvaEventoCom20Pontos() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.EXERCICIO_RESOLVIDO);
        request.setUsuarioId(1L);
        request.setReferenciaId(6L);
        request.setDificuldade(Dificuldade.MEDIO);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.EXERCICIO_RESOLVIDO, 6L, 1L)).thenReturn(false);
        when(conquistaRepository.findByCriterio(any())).thenReturn(Optional.empty());

        pontuacaoService.registrarEvento(request);

        ArgumentCaptor<PontosEvento> captor = ArgumentCaptor.forClass(PontosEvento.class);
        verify(pontosEventoRepository).save(captor.capture());
        assertThat(captor.getValue().getPontos()).isEqualTo(20);
    }

    @Test
    void registrarEvento_exercicioDificil_salvaEventoCom40Pontos() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.EXERCICIO_RESOLVIDO);
        request.setUsuarioId(1L);
        request.setReferenciaId(7L);
        request.setDificuldade(Dificuldade.DIFICIL);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.EXERCICIO_RESOLVIDO, 7L, 1L)).thenReturn(false);
        when(conquistaRepository.findByCriterio(any())).thenReturn(Optional.empty());

        pontuacaoService.registrarEvento(request);

        ArgumentCaptor<PontosEvento> captor = ArgumentCaptor.forClass(PontosEvento.class);
        verify(pontosEventoRepository).save(captor.capture());
        assertThat(captor.getValue().getPontos()).isEqualTo(40);
    }

    @Test
    void registrarEvento_eventoDuplicado_ignoraSilenciosamente() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.TOPICO_CRIADO);
        request.setUsuarioId(1L);
        request.setReferenciaId(10L);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.TOPICO_CRIADO, 10L, 1L)).thenReturn(true);

        pontuacaoService.registrarEvento(request);

        verify(pontosEventoRepository, never()).save(any());
    }

    @Test
    void registrarEvento_exercicioSemDificuldade_lancaRegraNegocio() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.EXERCICIO_RESOLVIDO);
        request.setUsuarioId(1L);
        request.setReferenciaId(5L);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.EXERCICIO_RESOLVIDO, 5L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> pontuacaoService.registrarEvento(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Dificuldade");
    }

    @Test
    void registrarEvento_primeiroTopico_desbloqueiaBadge() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.TOPICO_CRIADO);
        request.setUsuarioId(2L);
        request.setReferenciaId(11L);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.TOPICO_CRIADO, 11L, 2L)).thenReturn(false);

        Conquista badge = Conquista.builder().id(1L).criterio("PRIMEIRO_TOPICO")
                .nome("Primeiro Tópico").descricao("desc").build();
        when(conquistaRepository.findByCriterio("PRIMEIRO_TOPICO")).thenReturn(Optional.of(badge));
        when(conquistaRepository.findByCriterio(argThat(c -> !c.equals("PRIMEIRO_TOPICO"))))
                .thenReturn(Optional.empty());
        when(pontosEventoRepository.countByUsuarioIdAndTipo(2L, TipoEvento.TOPICO_CRIADO)).thenReturn(1L);
        when(usuarioConquistaRepository.existsByUsuarioIdAndConquistaId(2L, 1L)).thenReturn(false);

        pontuacaoService.registrarEvento(request);

        verify(usuarioConquistaRepository).save(any(UsuarioConquista.class));
    }

    @Test
    void registrarEvento_badgeJaDesbloqueada_naoSalvaNovamente() {
        EventoPontosRequest request = new EventoPontosRequest();
        request.setTipo(TipoEvento.TOPICO_CRIADO);
        request.setUsuarioId(2L);
        request.setReferenciaId(12L);

        when(pontosEventoRepository.existsByTipoAndReferenciaIdAndUsuarioId(
                TipoEvento.TOPICO_CRIADO, 12L, 2L)).thenReturn(false);

        Conquista badge = Conquista.builder().id(1L).criterio("PRIMEIRO_TOPICO")
                .nome("Primeiro Tópico").descricao("desc").build();
        when(conquistaRepository.findByCriterio("PRIMEIRO_TOPICO")).thenReturn(Optional.of(badge));
        when(conquistaRepository.findByCriterio(argThat(c -> !c.equals("PRIMEIRO_TOPICO"))))
                .thenReturn(Optional.empty());
        when(pontosEventoRepository.countByUsuarioIdAndTipo(2L, TipoEvento.TOPICO_CRIADO)).thenReturn(2L);
        when(usuarioConquistaRepository.existsByUsuarioIdAndConquistaId(2L, 1L)).thenReturn(true);

        pontuacaoService.registrarEvento(request);

        verify(usuarioConquistaRepository, never()).save(any());
    }

    @Test
    void extrato_retornaEventosOrdenadosPorData() {
        LocalDateTime agora = LocalDateTime.now();
        List<PontosEvento> eventos = List.of(
                PontosEvento.builder().tipo(TipoEvento.TOPICO_CRIADO).referenciaId(1L).pontos(5)
                        .criadoEm(agora).build(),
                PontosEvento.builder().tipo(TipoEvento.COMENTARIO).referenciaId(2L).pontos(3)
                        .criadoEm(agora.minusMinutes(5)).build()
        );
        when(pontosEventoRepository.findByUsuarioIdOrderByCriadoEmDesc(1L)).thenReturn(eventos);

        List<PontosEventoResponse> resultado = pontuacaoService.extrato(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTipo()).isEqualTo(TipoEvento.TOPICO_CRIADO);
        assertThat(resultado.get(0).getPontos()).isEqualTo(5);
    }

    @Test
    void conquistasDoUsuario_retornaConquistasDesbloqueadas() {
        LocalDateTime agora = LocalDateTime.now();
        UsuarioConquista uc = UsuarioConquista.builder()
                .id(1L).usuarioId(1L).conquistaId(10L).desbloqueadaEm(agora).build();
        Conquista conquista = Conquista.builder()
                .id(10L).nome("Primeiro Tópico").descricao("desc").criterio("PRIMEIRO_TOPICO").build();

        when(usuarioConquistaRepository.findByUsuarioId(1L)).thenReturn(List.of(uc));
        when(conquistaRepository.findById(10L)).thenReturn(Optional.of(conquista));

        List<ConquistaResponse> resultado = pontuacaoService.conquistasDoUsuario(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Primeiro Tópico");
        assertThat(resultado.get(0).getDesbloqueadaEm()).isEqualTo(agora);
    }
}
