package br.edu.ifsp.guarulhos.suporte_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaqResponse {
    private String pergunta;
    private String resposta;
}
