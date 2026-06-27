package br.edu.ifsp.guarulhos.auth_service.service;

import br.edu.ifsp.guarulhos.auth_service.dto.request.LoginRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.request.RegistroRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.response.AuthResponse;
import br.edu.ifsp.guarulhos.auth_service.exception.ContaInativaException;
import br.edu.ifsp.guarulhos.auth_service.exception.CredenciaisInvalidasException;
import br.edu.ifsp.guarulhos.auth_service.exception.EmailJaCadastradoException;
import br.edu.ifsp.guarulhos.auth_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.auth_service.model.Usuario;
import br.edu.ifsp.guarulhos.auth_service.model.enums.Perfil;
import br.edu.ifsp.guarulhos.auth_service.repository.UsuarioRepository;
import br.edu.ifsp.guarulhos.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Regras de negócio de autenticação: cadastro, login com geração de JWT e exclusão de
 * conta (LGPD), com senhas protegidas por hash bcrypt (US-19).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthResponse registrar(RegistroRequest request){
        if(usuarioRepository.existsByEmail(request.getEmail())){
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
        String token = jwtService.gerarToken(usuario.getId(), usuario.getPerfil().name());
        return new AuthResponse(token, usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }

    public AuthResponse login(LoginRequest request){
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CredenciaisInvalidasException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())){
            throw new CredenciaisInvalidasException("Email ou senha inválidos");
        }
        if(!usuario.isAtivo()){
            throw new ContaInativaException("Usuário inativo no sistema");
        }
        String token = jwtService.gerarToken(usuario.getId(), usuario.getPerfil().name());
        return new AuthResponse(token, usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }

    public void excluirConta(Long userId){
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }
}
