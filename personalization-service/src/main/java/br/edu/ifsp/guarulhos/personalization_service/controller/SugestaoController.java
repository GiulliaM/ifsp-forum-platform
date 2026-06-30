package br.edu.ifsp.guarulhos.personalization_service.controller;

import br.edu.ifsp.guarulhos.personalization_service.dto.SugestaoResponse;
import br.edu.ifsp.guarulhos.personalization_service.service.SugestaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sugestoes")
@RequiredArgsConstructor
public class SugestaoController {

    private final SugestaoService sugestaoService;

    @GetMapping
    public ResponseEntity<SugestaoResponse> sugerir(@RequestHeader("X-User-Id") Long usuarioId) {
        return ResponseEntity.ok(sugestaoService.sugerir(usuarioId));
    }
}
