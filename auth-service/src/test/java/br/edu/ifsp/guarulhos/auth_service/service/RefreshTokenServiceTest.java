package br.edu.ifsp.guarulhos.auth_service.service;

import br.edu.ifsp.guarulhos.auth_service.exception.RefreshTokenInvalidoException;
import br.edu.ifsp.guarulhos.auth_service.model.RefreshToken;
import br.edu.ifsp.guarulhos.auth_service.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpiration", 604800000L);
    }

    @Test
    void gerar_deveSalvarNovoRefreshToken() {
        RefreshToken salvo = RefreshToken.builder()
                .token("uuid-gerado")
                .usuarioId(1L)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revogado(false)
                .build();
        when(refreshTokenRepository.save(any())).thenReturn(salvo);

        RefreshToken resultado = refreshTokenService.gerar(1L);

        verify(refreshTokenRepository).revogarTodosPorUsuario(1L);
        verify(refreshTokenRepository).save(any());
        assertThat(resultado.getToken()).isEqualTo("uuid-gerado");
    }

    @Test
    void validar_tokenValido_deveRetornarToken() {
        RefreshToken token = RefreshToken.builder()
                .token("token-valido")
                .usuarioId(1L)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revogado(false)
                .build();
        when(refreshTokenRepository.findByToken("token-valido")).thenReturn(Optional.of(token));

        RefreshToken resultado = refreshTokenService.validar("token-valido");

        assertThat(resultado.getToken()).isEqualTo("token-valido");
    }

    @Test
    void validar_tokenNaoEncontrado_deveLancarExcecao() {
        when(refreshTokenRepository.findByToken("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.validar("inexistente"))
                .isInstanceOf(RefreshTokenInvalidoException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void validar_tokenRevogado_deveLancarExcecao() {
        RefreshToken token = RefreshToken.builder()
                .token("token-revogado")
                .usuarioId(1L)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revogado(true)
                .build();
        when(refreshTokenRepository.findByToken("token-revogado")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.validar("token-revogado"))
                .isInstanceOf(RefreshTokenInvalidoException.class)
                .hasMessageContaining("revogado");
    }

    @Test
    void validar_tokenExpirado_deveLancarExcecao() {
        RefreshToken token = RefreshToken.builder()
                .token("token-expirado")
                .usuarioId(1L)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .revogado(false)
                .build();
        when(refreshTokenRepository.findByToken("token-expirado")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.validar("token-expirado"))
                .isInstanceOf(RefreshTokenInvalidoException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void revogar_tokenExistente_deveMarcarComoRevogado() {
        RefreshToken token = RefreshToken.builder()
                .token("token-ativo")
                .usuarioId(1L)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revogado(false)
                .build();
        when(refreshTokenRepository.findByToken("token-ativo")).thenReturn(Optional.of(token));

        refreshTokenService.revogar("token-ativo");

        assertThat(token.isRevogado()).isTrue();
        verify(refreshTokenRepository).save(token);
    }
}
