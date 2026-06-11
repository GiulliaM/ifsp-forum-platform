package br.edu.ifsp.guarulhos.forum_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor

public class ComentarioResponse {

    private Long id;
    private String conteudo;
    private Long autorId;
    private Long parentId;
    private long totalLikes;
    private LocalDateTime criadoEm;
    private LocalDateTime editadoEm;
}
