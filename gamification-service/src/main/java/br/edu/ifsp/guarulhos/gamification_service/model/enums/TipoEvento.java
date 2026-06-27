package br.edu.ifsp.guarulhos.gamification_service.model.enums;

/**
 * Tipo de ação pontuável recebida dos demais serviços. O valor em pontos de cada tipo
 * é decidido aqui no gamification-service, não no serviço de origem (US-12).
 */
public enum TipoEvento {
    TOPICO_CRIADO,
    COMENTARIO,
    LIKE_RECEBIDO,
    EXERCICIO_RESOLVIDO
}
