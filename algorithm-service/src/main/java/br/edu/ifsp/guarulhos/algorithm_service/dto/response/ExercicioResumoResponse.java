package br.edu.ifsp.guarulhos.algorithm_service.dto.response;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusPessoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ExercicioResumoResponse {

    private Long id;
    private String titulo;
    private Dificuldade dificuldade;
    private String categoria;
    private double taxaAcerto;
    private StatusPessoal statusPessoal;
}
