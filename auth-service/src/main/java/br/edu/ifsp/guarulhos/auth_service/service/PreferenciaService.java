package br.edu.ifsp.guarulhos.auth_service.service;

import br.edu.ifsp.guarulhos.auth_service.dto.request.PreferenciaRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.response.PreferenciaResponse;
import br.edu.ifsp.guarulhos.auth_service.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.guarulhos.auth_service.model.PreferenciaUsuario;
import br.edu.ifsp.guarulhos.auth_service.repository.PreferenciaUsuarioRepository;
import br.edu.ifsp.guarulhos.auth_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenciaService {

    private final PreferenciaUsuarioRepository preferenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public PreferenciaResponse buscar(Long usuarioId) {
        validarUsuario(usuarioId);
        PreferenciaUsuario pref = preferenciaRepository.findById(usuarioId)
                .orElse(new PreferenciaUsuario(usuarioId, null, List.of(), List.of()));
        return toResponse(pref);
    }

    @Transactional
    public PreferenciaResponse salvar(Long usuarioId, PreferenciaRequest request) {
        validarUsuario(usuarioId);
        PreferenciaUsuario pref = preferenciaRepository.findById(usuarioId)
                .orElse(PreferenciaUsuario.builder().usuarioId(usuarioId).build());

        pref.setNivel(request.getNivel());
        pref.setInteresses(request.getInteresses() != null ? request.getInteresses() : List.of());
        pref.setLinguagens(request.getLinguagens() != null ? request.getLinguagens() : List.of());

        return toResponse(preferenciaRepository.save(pref));
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }
    }

    private PreferenciaResponse toResponse(PreferenciaUsuario pref) {
        return new PreferenciaResponse(pref.getUsuarioId(), pref.getNivel(),
                pref.getInteresses(), pref.getLinguagens());
    }
}
