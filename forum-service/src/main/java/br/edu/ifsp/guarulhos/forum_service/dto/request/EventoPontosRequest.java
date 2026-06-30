package br.edu.ifsp.guarulhos.forum_service.dto.request;

import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoEventoPontos;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Corpo enviado ao gamification-service em POST /api/pontos/eventos. */
@Data
@AllArgsConstructor
public class EventoPontosRequest {
    private TipoEventoPontos tipo;
    private Long usuarioId;
    private Long referenciaId;
}