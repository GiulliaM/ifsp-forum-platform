package br.edu.ifsp.guarulhos.personalization_service.dto;

import lombok.Data;

@Data
public class TopicoSugestaoResponse {
    private Long id;
    private String titulo;
    private String categoria;
    private long totalLikes;
    private long totalComentarios;
}
