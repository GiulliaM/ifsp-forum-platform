package br.edu.ifsp.guarulhos.gamification_service.dto.response;

import br.edu.ifsp.guarulhos.gamification_service.model.enums.TipoEvento;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PontosEventoResponse {

    private TipoEvento tipo;
    private Long referenciaId;
    private int pontos;
    private LocalDateTime criadoEm;
}