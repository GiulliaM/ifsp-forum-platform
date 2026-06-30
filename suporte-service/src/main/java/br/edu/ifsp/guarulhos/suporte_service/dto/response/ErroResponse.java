package br.edu.ifsp.guarulhos.suporte_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErroResponse {
    private int status;
    private String mensagem;
    private LocalDateTime timestamp;
}
