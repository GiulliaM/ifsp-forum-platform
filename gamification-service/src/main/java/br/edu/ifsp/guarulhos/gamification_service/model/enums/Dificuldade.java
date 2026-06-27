package br.edu.ifsp.guarulhos.gamification_service.model.enums;

/**
 * Dificuldade do exercício resolvido, enviada junto do evento EXERCICIO_RESOLVIDO para
 * que a pontuação correta seja atribuída (fácil/médio/difícil → +10/+20/+40 — US-12).
 */
public enum Dificuldade {
    FACIL,
    MEDIO,
    DIFICIL
}
