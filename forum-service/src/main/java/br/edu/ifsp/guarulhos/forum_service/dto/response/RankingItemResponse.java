package br.edu.ifsp.guarulhos.forum_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingItemResponse {
    private int posicao;
    private Long usuarioId;
    private long totalPontos;
}
