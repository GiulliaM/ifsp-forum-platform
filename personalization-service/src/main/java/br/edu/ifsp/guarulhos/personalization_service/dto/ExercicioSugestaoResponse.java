package br.edu.ifsp.guarulhos.personalization_service.dto;

import lombok.Data;

@Data
public class ExercicioSugestaoResponse {
    private Long id;
    private String titulo;
    private String dificuldade;
    private String categoria;
    private double taxaAcerto;
}
