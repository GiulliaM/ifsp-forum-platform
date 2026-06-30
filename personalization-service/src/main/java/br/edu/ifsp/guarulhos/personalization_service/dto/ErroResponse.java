package br.edu.ifsp.guarulhos.personalization_service.dto;

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
