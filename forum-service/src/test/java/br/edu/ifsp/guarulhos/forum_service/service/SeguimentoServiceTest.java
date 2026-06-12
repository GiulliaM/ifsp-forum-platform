package br.edu.ifsp.guarulhos.forum_service.service;

import br.edu.ifsp.guarulhos.forum_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.forum_service.exception.RegraNegocioException;
import br.edu.ifsp.guarulhos.forum_service.model.Seguimento;
import br.edu.ifsp.guarulhos.forum_service.model.Topico;
import br.edu.ifsp.guarulhos.forum_service.repository.SeguimentoRepository;
import br.edu.ifsp.guarulhos.forum_service.repository.TopicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
}
