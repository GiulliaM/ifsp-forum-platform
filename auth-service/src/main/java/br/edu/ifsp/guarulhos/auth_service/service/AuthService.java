package br.edu.ifsp.guarulhos.auth_service.service;

import br.edu.ifsp.guarulhos.auth_service.dto.request.LoginRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.request.RegistroRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.response.AuthResponse;
import br.edu.ifsp.guarulhos.auth_service.exception.ContaInativaException;
import br.edu.ifsp.guarulhos.auth_service.exception.CredenciaisInvalidasException;
import br.edu.ifsp.guarulhos.auth_service.exception.EmailJaCadastradoException;
import br.edu.ifsp.guarulhos.auth_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.auth_service.model.RefreshToken;
import br.edu.ifsp.guarulhos.auth_service.model.Usuario;
import br.edu.ifsp.guarulhos.auth_service.model.enums.Perfil;
import br.edu.ifsp.guarulhos.auth_service.repository.UsuarioRepository;
import br.edu.ifsp.guarulhos.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado no sistema");
        }
        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .perfil(Perfil.ESTUDANTE)
                .termosAceitos(request.isTermosAceitos())
                .build();

        usuarioRepository.save(usuario);
        return buildAuthResponse(usuario);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredenciaisInvalidasException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Email ou senha inválidos");
        }
        if (!usuario.isAtivo()) {
            throw new ContaInativaException("Usuário inativo no sistema");
        }
        return buildAuthResponse(usuario);
    }

    @Transactional
    public AuthResponse renovarToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.validar(refreshTokenStr);

        Usuario usuario = usuarioRepository.findById(refreshToken.getUsuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!usuario.isAtivo()) {
            throw new ContaInativaException("Usuário inativo no sistema");
        }

        return buildAuthResponse(usuario);
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenService.revogar(refreshTokenStr);
    }

    public void excluirConta(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        String accessToken = jwtService.gerarToken(usuario.getId(), usuario.getPerfil().name());
        RefreshToken refreshToken = refreshTokenService.gerar(usuario.getId());
        long expiresIn = jwtExpiration / 1000;
        return new AuthResponse(accessToken, refreshToken.getToken(), expiresIn,
                usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }
}
