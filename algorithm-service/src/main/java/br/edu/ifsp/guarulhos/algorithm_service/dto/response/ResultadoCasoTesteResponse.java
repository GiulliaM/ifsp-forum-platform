package br.edu.ifsp.guarulhos.algorithm_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResultadoCasoTesteResponse {

    private int numero;
    private boolean passou;
    private String entrada;
    private String saidaEsperada;
    private String saidaObtida;
}
