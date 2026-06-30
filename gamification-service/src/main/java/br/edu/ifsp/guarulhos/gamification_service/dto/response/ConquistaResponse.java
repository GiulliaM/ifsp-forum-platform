package br.edu.ifsp.guarulhos.gamification_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConquistaResponse {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDateTime desbloqueadaEm;
}