package br.edu.ifsp.guarulhos.personalization_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreferenciaResponse {
    private Long usuarioId;
    private String nivel;
    private List<String> interesses;
    private List<String> linguagens;
}
