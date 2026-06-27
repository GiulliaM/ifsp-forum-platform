package br.edu.ifsp.guarulhos.algorithm_service.dto.response;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusPessoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Detalhe de um exercício, com enunciado, restrições e exemplos de entrada/saída (US-07 CA3).
 */
@Data
@Builder
@AllArgsConstructor
public class ExercicioDetalheResponse {

    private Long id;
    private String titulo;
    private String enunciado;
    private Dificuldade dificuldade;
    private String categoria;
    private String restricoes;
    private String exemploEntrada;
    private String exemploSaida;
    private double taxaAcerto;
    private long totalCasosTeste;
    private StatusPessoal statusPessoal;
}
