package br.edu.ifsp.guarulhos.forum_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor

public class TopicoResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private String categoria;
    private Long autorId;
    private boolean fixado;
    private boolean encerrado;
    private long totalLikes;
    private long totalComentarios;
    private LocalDateTime criadoEm;
    private boolean temNovidades;
}
