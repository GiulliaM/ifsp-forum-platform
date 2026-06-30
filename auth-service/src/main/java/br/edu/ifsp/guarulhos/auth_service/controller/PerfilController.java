package br.edu.ifsp.guarulhos.auth_service.controller;

import br.edu.ifsp.guarulhos.auth_service.dto.request.PreferenciaRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.response.PreferenciaResponse;
import br.edu.ifsp.guarulhos.auth_service.service.PreferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/usuarios")
@RequiredArgsConstructor
public class PerfilController {

    private final PreferenciaService preferenciaService;

    @GetMapping("/preferencias")
    public ResponseEntity<PreferenciaResponse> buscar(@RequestHeader("X-User-Id") Long usuarioId) {
        return ResponseEntity.ok(preferenciaService.buscar(usuarioId));
    }

    @PutMapping("/preferencias")
    public ResponseEntity<PreferenciaResponse> salvar(
            @RequestHeader("X-User-Id") Long usuarioId,
            @Valid @RequestBody PreferenciaRequest request) {
        return ResponseEntity.ok(preferenciaService.salvar(usuarioId, request));
    }
}
