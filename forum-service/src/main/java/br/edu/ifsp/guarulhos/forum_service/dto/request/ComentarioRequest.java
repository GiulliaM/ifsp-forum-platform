package br.edu.ifsp.guarulhos.forum_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComentarioRequest {

    @NotBlank(message = "Conteúdo é obrigatório")
    private String conteudo;

    private Long parentId;

    private String imageUrl;
}
