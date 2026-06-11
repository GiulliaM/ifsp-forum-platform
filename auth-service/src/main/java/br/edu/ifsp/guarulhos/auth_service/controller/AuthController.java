package br.edu.ifsp.guarulhos.auth_service.controller;

import br.edu.ifsp.guarulhos.auth_service.dto.request.LoginRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.request.RegistroRequest;
import br.edu.ifsp.guarulhos.auth_service.dto.response.AuthResponse;
import br.edu.ifsp.guarulhos.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request){
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/usuarios/deletar")
    public ResponseEntity<Void> excluirConta(@RequestHeader("X-User-Id") Long id){
        authService.excluirConta(id);
        return ResponseEntity.noContent().build();
    }
}
