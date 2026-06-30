package br.edu.ifsp.guarulhos.auth_service.service;

import br.edu.ifsp.guarulhos.auth_service.dto.request.PreferenciaRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.response.PreferenciaResponse;
import br.edu.ifsp.guarulhos.auth_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.auth_service.model.PreferenciaUsuario;
import br.edu.ifsp.guarulhos.auth_service.model.enums.NivelAprendizado;
import br.edu.ifsp.guarulhos.auth_service.repository.PreferenciaUsuarioRepository;
import br.edu.ifsp.guarulhos.auth_service.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenciaServiceTest {

    @Mock
    private PreferenciaUsuarioRepository preferenciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PreferenciaService preferenciaService;

    @Test
    void buscar_usuarioSemPreferencias_retornaPreferenciaVazia() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(preferenciaRepository.findById(1L)).thenReturn(Optional.empty());

        PreferenciaResponse resp = preferenciaService.buscar(1L);

        assertThat(resp.getUsuarioId()).isEqualTo(1L);
        assertThat(resp.getNivel()).isNull();
        assertThat(resp.getInteresses()).isEmpty();
        assertThat(resp.getLinguagens()).isEmpty();
    }

    @Test
    void buscar_usuarioComPreferencias_retornaDados() {
        PreferenciaUsuario pref = PreferenciaUsuario.builder()
                .usuarioId(1L)
                .nivel(NivelAprendizado.INTERMEDIARIO)
                .interesses(List.of("backend", "algoritmos"))
                .linguagens(List.of("Java", "Python"))
                .build();
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(preferenciaRepository.findById(1L)).thenReturn(Optional.of(pref));

        PreferenciaResponse resp = preferenciaService.buscar(1L);

        assertThat(resp.getNivel()).isEqualTo(NivelAprendizado.INTERMEDIARIO);
        assertThat(resp.getInteresses()).containsExactly("backend", "algoritmos");
        assertThat(resp.getLinguagens()).containsExactly("Java", "Python");
    }

    @Test
    void buscar_usuarioInexistente_lancaExcecao() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> preferenciaService.buscar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void salvar_novasPreferencias_persisteERetorna() {
        PreferenciaRequest request = new PreferenciaRequest();
        request.setNivel(NivelAprendizado.AVANCADO);
        request.setInteresses(List.of("frontend"));
        request.setLinguagens(List.of("JavaScript"));

        PreferenciaUsuario salvo = PreferenciaUsuario.builder()
                .usuarioId(1L)
                .nivel(NivelAprendizado.AVANCADO)
                .interesses(List.of("frontend"))
                .linguagens(List.of("JavaScript"))
                .build();

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(preferenciaRepository.findById(1L)).thenReturn(Optional.empty());
        when(preferenciaRepository.save(any())).thenReturn(salvo);

        PreferenciaResponse resp = preferenciaService.salvar(1L, request);

        assertThat(resp.getNivel()).isEqualTo(NivelAprendizado.AVANCADO);
        assertThat(resp.getInteresses()).containsExactly("frontend");
        verify(preferenciaRepository).save(any());
    }

    @Test
    void salvar_atualizaPreferenciasExistentes() {
        PreferenciaUsuario existente = PreferenciaUsuario.builder()
                .usuarioId(1L)
                .nivel(NivelAprendizado.INICIANTE)
                .interesses(List.of("backend"))
                .linguagens(List.of("Java"))
                .build();

        PreferenciaRequest request = new PreferenciaRequest();
        request.setNivel(NivelAprendizado.INTERMEDIARIO);
        request.setInteresses(List.of("backend", "cloud"));
        request.setLinguagens(List.of("Java", "Go"));

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(preferenciaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(preferenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PreferenciaResponse resp = preferenciaService.salvar(1L, request);

        assertThat(resp.getNivel()).isEqualTo(NivelAprendizado.INTERMEDIARIO);
        assertThat(resp.getInteresses()).containsExactly("backend", "cloud");
        assertThat(resp.getLinguagens()).containsExactly("Java", "Go");
    }
}
