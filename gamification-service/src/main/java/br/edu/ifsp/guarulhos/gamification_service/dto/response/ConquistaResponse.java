package br.edu.ifsp.guarulhos.gamification_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Conquista desbloqueada exibida no perfil do usuário (US-12, CA3).
 */
@Data
@Builder
public class ConquistaResponse {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDateTime desbloqueadaEm;
}