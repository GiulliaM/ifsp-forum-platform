package br.edu.ifsp.guarulhos.forum_service.controller;

import br.edu.ifsp.guarulhos.forum_service.dto.response.TopicoResponse;
import br.edu.ifsp.guarulhos.forum_service.service.SeguimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topicos")
@RequiredArgsConstructor
public class UsuarioController {

    private final SeguimentoService seguimentoService;

    @GetMapping("/seguidos")
    public ResponseEntity<List<TopicoResponse>> topicosSeguidos(
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, defaultValue = "desc") String ordem){
        return ResponseEntity.ok(seguimentoService.topicosSeguidos(usuarioId, categoria, ordem));
    }
}
