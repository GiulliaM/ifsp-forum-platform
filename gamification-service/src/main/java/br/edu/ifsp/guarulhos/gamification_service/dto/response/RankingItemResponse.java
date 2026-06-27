package br.edu.ifsp.guarulhos.gamification_service.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * Linha do ranking (US-11): posição, usuário e pontuação acumulada no escopo/período
 * consultados. O nome e o avatar são resolvidos pelo consumidor a partir do usuarioId.
 */
@Data
@Builder
public class RankingItemResponse {

    private long posicao;
    private Long usuarioId;
    private long pontuacao;
}