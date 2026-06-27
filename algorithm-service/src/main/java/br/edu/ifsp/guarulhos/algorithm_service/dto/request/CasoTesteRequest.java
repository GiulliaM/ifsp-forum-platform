package br.edu.ifsp.guarulhos.algorithm_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Dados de um caso de teste informado no cadastro de um exercício (US-10 CA1/CA2).
 */
@Data
public class CasoTesteRequest {

    private String entrada;

    @NotNull(message = "A saída esperada do caso de teste é obrigatória")
    private String saidaEsperada;

    private boolean oculto = true;
}
