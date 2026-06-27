package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.model.Seguimento;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.repository.ComentarioRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.SeguimentoRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeguimentoServiceTest {

    @Mock
    private SeguimentoRepository seguimentoRepository;
    @Mock
    private TopicoRepository topicoRepository;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private TopicoService topicoService;
    @InjectMocks
    private SeguimentoService seguimentoService;

    @Test
    void seguir_quandoAindaNaoSegue_salvaSeguimento() {
        Topico topico = Topico.builder().id(1L).build();
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(seguimentoRepository.findByUsuarioIdAndTopicoId(5L, 1L)).thenReturn(Optional.empty());

        seguimentoService.seguir(1L, 5L);

        verify(seguimentoRepository).save(any(Seguimento.class));
    }

    @Test
    void seguir_quandoJaSegue_lancaRegraNegocio() {
        Topico topico = Topico.builder().id(1L).build();
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(seguimentoRepository.findByUsuarioIdAndTopicoId(5L, 1L))
                .thenReturn(Optional.of(Seguimento.builder().id(1L).usuarioId(5L).topico(topico).build()));

        assertThatThrownBy(() -> seguimentoService.seguir(1L, 5L))
                .isInstanceOf(RegraNegocioException.class);
        verify(seguimentoRepository, never()).save(any());
    }

    @Test
    void deixarDeSeguir_quandoNaoSegue_lancaRecursoNaoEncontrado() {
        when(seguimentoRepository.findByUsuarioIdAndTopicoId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seguimentoService.deixarDeSeguir(1L, 5L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void topicosSeguidos_filtraPorCategoriaEMarcaNovidades() {
        Topico java = Topico.builder().id(1L).titulo("Tópico Java").categoria("Java")
                .criadoEm(LocalDateTime.now().minusDays(1)).build();
        Topico python = Topico.builder().id(2L).titulo("Tópico Python").categoria("Python")
                .criadoEm(LocalDateTime.now()).build();
        Seguimento segJava = Seguimento.builder().id(1L).usuarioId(5L).topico(java)
                .seguidoEm(LocalDateTime.now().minusDays(2)).build();
        Seguimento segPython = Seguimento.builder().id(2L).usuarioId(5L).topico(python)
                .seguidoEm(LocalDateTime.now().minusDays(2)).build();
        when(seguimentoRepository.findByUsuarioId(5L)).thenReturn(List.of(segJava, segPython));
        when(topicoService.montarResponse(java)).thenReturn(
                TopicoResponse.builder().id(1L).titulo("Tópico Java").categoria("Java")
                        .criadoEm(java.getCriadoEm()).build());
        when(comentarioRepository.existsByTopicoIdAndCriadoEmAfter(eq(1L), any())).thenReturn(true);

        List<TopicoResponse> seguidos = seguimentoService.topicosSeguidos(5L, "Java", "desc");

        assertThat(seguidos).hasSize(1);
        assertThat(seguidos.get(0).getCategoria()).isEqualTo("Java");
        assertThat(seguidos.get(0).isTemNovidades()).isTrue();
    }

    @Test
    void topicosSeguidos_ordenaPorDataDescendentePorPadrao() {
        Topico antigo = Topico.builder().id(1L).titulo("Antigo").categoria("Java")
                .criadoEm(LocalDateTime.now().minusDays(3)).build();
        Topico recente = Topico.builder().id(2L).titulo("Recente").categoria("Java")
                .criadoEm(LocalDateTime.now()).build();
        Seguimento s1 = Seguimento.builder().id(1L).usuarioId(5L).topico(antigo)
                .seguidoEm(LocalDateTime.now()).build();
        Seguimento s2 = Seguimento.builder().id(2L).usuarioId(5L).topico(recente)
                .seguidoEm(LocalDateTime.now()).build();
        when(seguimentoRepository.findByUsuarioId(5L)).thenReturn(List.of(s1, s2));
        when(topicoService.montarResponse(antigo)).thenReturn(
                TopicoResponse.builder().id(1L).titulo("Antigo").criadoEm(antigo.getCriadoEm()).build());
        when(topicoService.montarResponse(recente)).thenReturn(
                TopicoResponse.builder().id(2L).titulo("Recente").criadoEm(recente.getCriadoEm()).build());
        when(comentarioRepository.existsByTopicoIdAndCriadoEmAfter(any(), any())).thenReturn(false);

        List<TopicoResponse> seguidos = seguimentoService.topicosSeguidos(5L, null, "desc");

        assertThat(seguidos.get(0).getTitulo()).isEqualTo("Recente");
        assertThat(seguidos.get(1).getTitulo()).isEqualTo("Antigo");
    }
}
