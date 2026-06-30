package br.edu.ifsp.guarulhos.gamification_service.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * O nome e o avatar do usuário são resolvidos pelo consumidor a partir do usuarioId.
 */
@Data
@Builder
public class RankingItemResponse {

    private long posicao;
    private Long usuarioId;
    private long pontuacao;
}