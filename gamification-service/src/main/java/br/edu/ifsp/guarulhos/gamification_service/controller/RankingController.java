package br.edu.ifsp.guarulhos.gamification_service.controller;

import br.edu.ifsp.guarulhos.gamification_service.dto.response.RankingItemResponse;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.Escopo;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.Periodo;
import br.edu.ifsp.guarulhos.gamification_service.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<RankingItemResponse>> consultar(
            @RequestParam(defaultValue = "GERAL") Escopo escopo,
            @RequestParam(defaultValue = "TOTAL") Periodo periodo){
        return ResponseEntity.ok(rankingService.consultar(escopo, periodo));
    }
}
