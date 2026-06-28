package br.edu.ifsp.guarulhos.forum_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicoRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 10, message = "Título deve ter no mínimo 10 caracteres")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 30, message = "Descrição deve ter no mínimo 30 caracteres")
    private String descricao;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    private String imageUrl;
}
