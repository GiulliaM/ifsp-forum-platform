package br.edu.ifsp.guarulhos.forum_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricaCategoriaResponse {
    private String categoria;
    private long totalTopicosSemResposta;
    private int periodoDias;
}
