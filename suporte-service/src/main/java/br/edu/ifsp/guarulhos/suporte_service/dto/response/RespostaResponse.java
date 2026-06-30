package br.edu.ifsp.guarulhos.suporte_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RespostaResponse {
    private Long moderadorId;
    private String mensagem;
    private LocalDateTime criadoEm;
}
