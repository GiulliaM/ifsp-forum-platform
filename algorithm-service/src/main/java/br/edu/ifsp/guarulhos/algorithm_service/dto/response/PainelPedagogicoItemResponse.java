package br.edu.ifsp.guarulhos.algorithm_service.dto.response;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PainelPedagogicoItemResponse {

    private Long id;
    private String titulo;
    private Dificuldade dificuldade;
    private String categoria;
    private double taxaAcerto;
    private double taxaErro;
    private long totalSubmissoes;
}
