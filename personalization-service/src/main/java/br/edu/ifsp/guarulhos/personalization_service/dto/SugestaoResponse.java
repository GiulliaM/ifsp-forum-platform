package br.edu.ifsp.guarulhos.personalization_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SugestaoResponse {
    private List<TopicoSugestaoResponse> topicos;
    private List<ExercicioSugestaoResponse> exercicios;
}
