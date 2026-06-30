package br.edu.ifsp.guarulhos.auth_service.service;

import br.edu.ifsp.guarulhos.auth_service.exception.RefreshTokenInvalidoException;
import br.edu.ifsp.guarulhos.auth_service.model.RefreshToken;
import br.edu.ifsp.guarulhos.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Transactional
    public RefreshToken gerar(Long usuarioId) {
        refreshTokenRepository.revogarTodosPorUsuario(usuarioId);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuarioId(usuarioId)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .revogado(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validar(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token não encontrado"));

        if (refreshToken.isRevogado()) {
            throw new RefreshTokenInvalidoException("Refresh token já foi revogado");
        }
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenInvalidoException("Refresh token expirado");
        }
        return refreshToken;
    }

    @Transactional
    public void revogar(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevogado(true);
            refreshTokenRepository.save(rt);
        });
    }
}
