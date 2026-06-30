package br.edu.ifsp.guarulhos.algorithm_service.dto.request;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ExercicioRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 5, message = "Título deve ter no mínimo 5 caracteres")
    private String titulo;

    @NotBlank(message = "Enunciado é obrigatório")
    @Size(min = 20, message = "Enunciado deve ter no mínimo 20 caracteres")
    private String enunciado;

    @NotNull(message = "Dificuldade é obrigatória (FACIL, MEDIO ou DIFICIL)")
    private Dificuldade dificuldade;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    private String restricoes;
    private String exemploEntrada;
    private String exemploSaida;

    @NotEmpty(message = "Informe ao menos um caso de teste")
    @Valid
    private List<CasoTesteRequest> casosTeste;

    private boolean publicar = true;
}
