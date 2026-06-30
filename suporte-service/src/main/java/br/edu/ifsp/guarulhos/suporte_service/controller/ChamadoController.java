package br.edu.ifsp.guarulhos.suporte_service.controller;

import br.edu.ifsp.guarulhos.suporte_service.dto.request.AbrirChamadoRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.request.AlterarStatusRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.request.ResponderChamadoRequest;
import br.edu.ifsp.guarulhos.suporte_service.dto.response.ChamadoResponse;
import br.edu.ifsp.guarulhos.suporte_service.dto.response.ChamadoResumoResponse;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import br.edu.ifsp.guarulhos.suporte_service.service.ChamadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suporte/chamados")
@RequiredArgsConstructor
public class ChamadoController {

    private final ChamadoService chamadoService;

    @PostMapping
    public ResponseEntity<ChamadoResponse> abrir(
            @Valid @RequestBody AbrirChamadoRequest request,
            @RequestHeader("X-User-Id") Long usuarioId){
        ChamadoResponse response = chamadoService.abrirChamado(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ChamadoResumoResponse>> meusChamados(
            @RequestHeader("X-User-Id") Long usuarioId){
        return ResponseEntity.ok(chamadoService.listarMeusChamados(usuarioId));
    }

    @GetMapping("/{protocolo}")
    public ResponseEntity<ChamadoResponse> buscarPorProtocolo(
            @PathVariable String protocolo,
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestHeader("X-User-Role") String perfil){
        return ResponseEntity.ok(chamadoService.buscarPorProtocolo(protocolo, usuarioId, perfil));
    }

    @GetMapping
    public ResponseEntity<List<ChamadoResumoResponse>> painel(
            @RequestParam(required = false) StatusChamado status,
            @RequestHeader("X-User-Role") String perfil){
        return ResponseEntity.ok(chamadoService.listarPainel(status, perfil));
    }

    @PostMapping("/{id}/respostas")
    public ResponseEntity<ChamadoResponse> responder(
            @PathVariable Long id,
            @Valid @RequestBody ResponderChamadoRequest request,
            @RequestHeader("X-User-Id") Long moderadorId,
            @RequestHeader("X-User-Role") String perfil){
        return ResponseEntity.ok(chamadoService.responder(id, request, moderadorId, perfil));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ChamadoResponse> alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AlterarStatusRequest request,
            @RequestHeader("X-User-Role") String perfil){
        return ResponseEntity.ok(chamadoService.alterarStatus(id, request, perfil));
    }
}
