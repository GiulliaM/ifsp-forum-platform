package br.edu.ifsp.guarulhos.gamification_service.dto.request;

import br.edu.ifsp.guarulhos.gamification_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.TipoEvento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Evento pontuável enviado pelos serviços de origem (forum/algorithm). O emissor informa
 * apenas o que aconteceu; o valor em pontos é calculado aqui (US-12).
 */
@Data
public class EventoPontosRequest {

    @NotNull(message = "Tipo do evento é obrigatório")
    private TipoEvento tipo;

    @NotNull(message = "Usuário que recebe os pontos é obrigatório")
    private Long usuarioId;

    /** Id do tópico/comentário/exercício de origem; usado também para idempotência. */
    private Long referenciaId;

    /** Obrigatório apenas para EXERCICIO_RESOLVIDO, define a faixa de pontos. */
    private Dificuldade dificuldade;
}
