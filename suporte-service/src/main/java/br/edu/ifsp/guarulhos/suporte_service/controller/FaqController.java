package br.edu.ifsp.guarulhos.suporte_service.controller;

import br.edu.ifsp.guarulhos.suporte_service.dto.response.FaqResponse;
import br.edu.ifsp.guarulhos.suporte_service.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suporte/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FaqResponse>> listar(){
        return ResponseEntity.ok(faqService.listar());
    }
}
