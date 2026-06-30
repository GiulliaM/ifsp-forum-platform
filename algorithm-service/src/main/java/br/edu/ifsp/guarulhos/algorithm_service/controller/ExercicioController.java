package br.edu.ifsp.guarulhos.algorithm_service.controller;

import br.edu.ifsp.guarulhos.algorithm_service.dto.request.ExercicioRequest;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ExercicioDetalheResponse;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ExercicioResumoResponse;
import br.edu.ifsp.guarulhos.algorithm_service.dto.response.PainelPedagogicoItemResponse;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.service.ExercicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercicios")
@RequiredArgsConstructor
public class ExercicioController {

    private final ExercicioService exercicioService;

    @GetMapping
    public ResponseEntity<List<ExercicioResumoResponse>> listar(
            @RequestParam(required = false) Dificuldade dificuldade,
            @RequestParam(required = false) String categoria,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId){
        return ResponseEntity.ok(exercicioService.listar(dificuldade, categoria, usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExercicioDetalheResponse> buscarPorId(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId){
        return ResponseEntity.ok(exercicioService.buscarPorId(id, usuarioId));
    }

    @GetMapping("/painel-pedagogico")
    public ResponseEntity<List<PainelPedagogicoItemResponse>> painelPedagogico(
            @RequestParam(defaultValue = "10") int limite,
            @RequestHeader("X-User-Role") String perfil) {
        return ResponseEntity.ok(exercicioService.listarPainelPedagogico(limite, perfil));
    }

    @PostMapping
    public ResponseEntity<ExercicioDetalheResponse> cadastrar(
            @Valid @RequestBody ExercicioRequest request,
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestHeader("X-User-Role") String perfil){
        ExercicioDetalheResponse response = exercicioService.criar(request, perfil, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
