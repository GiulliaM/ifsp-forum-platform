package br.edu.ifsp.guarulhos.gamification_service.controller;

import br.edu.ifsp.guarulhos.gamification_service.dto.request.EventoPontosRequest;
import br.edu.ifsp.guarulhos.gamification_service.dto.response.ConquistaResponse;
import br.edu.ifsp.guarulhos.gamification_service.dto.response.PontosEventoResponse;
import br.edu.ifsp.guarulhos.gamification_service.service.PontuacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
public class PontosController {

    private final PontuacaoService pontuacaoService;

    /** Chamado internamente por forum-service e algorithm-service quando ocorre uma ação pontuável. */
    @PostMapping("/eventos")
    public ResponseEntity<Void> registrarEvento(@Valid @RequestBody EventoPontosRequest request){
        pontuacaoService.registrarEvento(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<PontosEventoResponse>> meuExtrato(
            @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(pontuacaoService.extrato(usuarioId));
    }

    @GetMapping("/me/conquistas")
    public ResponseEntity<List<ConquistaResponse>> minhasConquistas(
            @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(pontuacaoService.conquistasDoUsuario(usuarioId));
    }
}
