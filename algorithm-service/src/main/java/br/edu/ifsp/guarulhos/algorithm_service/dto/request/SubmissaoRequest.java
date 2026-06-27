package br.edu.ifsp.guarulhos.algorithm_service.dto.request;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Linguagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Dados de uma submissão de solução. Como o juiz é por comparação, além do código o
 * estudante envia a lista de saídas produzidas, uma por caso de teste, na ordem (US-08).
 */
@Data
public class SubmissaoRequest {

    @NotNull(message = "Exercício é obrigatório")
    private Long exercicioId;

    @NotNull(message = "Linguagem é obrigatória (JAVA, PYTHON ou CPP)")
    private Linguagem linguagem;

    @NotBlank(message = "O código da solução é obrigatório")
    private String codigo;

    private List<String> saidas;
}
