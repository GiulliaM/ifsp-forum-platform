package br.edu.ifsp.guarulhos.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Corpo padronizado de erro retornado pelo tratador global de exceções.
 */
@Data
@AllArgsConstructor
public class ErroResponse {

    private int status;
    private String mensagem;
    private LocalDateTime timestamp;
}
