package br.edu.ifsp.guarulhos.forum_service.model.enums;

/**
 * Espelha os valores de TipoEvento do gamification-service. O forum-service só emite
 * estes três; EXERCICIO_RESOLVIDO é emitido pelo algorithm-service.
 */
public enum TipoEventoPontos {
    TOPICO_CRIADO,
    COMENTARIO,
    LIKE_RECEBIDO
}