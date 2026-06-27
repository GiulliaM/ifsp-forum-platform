package br.edu.ifsp.guarulhos.algorithm_service.controller;

import br.edu.ifsp.guarulhos.algorithm_service.dto.request.SubmissaoRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.SubmissaoResponse;
import br.edu.ifsp.guarulhos.algorithm_service.service.SubmissaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST das submissões: envio de solução e histórico do estudante (US-08) e
 * feedback detalhado por caso de teste (US-09).
 */
@RestController
@RequestMapping("/api/submissoes")
@RequiredArgsConstructor
public class SubmissaoController {

    private final SubmissaoService submissaoService;

    @PostMapping
    public ResponseEntity<SubmissaoResponse> submeter(
            @Valid @RequestBody SubmissaoRequest request,
            @RequestHeader("X-User-Id") Long usuarioId){
        SubmissaoResponse response = submissaoService.submeter(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<SubmissaoResponse>> historico(
            @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(submissaoService.historico(usuarioId));
    }

    @GetMapping("/{id}/feedback")
    public ResponseEntity<SubmissaoResponse> feedback(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(submissaoService.feedback(id, usuarioId));
    }
}
