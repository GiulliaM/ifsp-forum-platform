package br.edu.ifsp.guarulhos.gamification_service.repository;

/**
 * Projeção da consulta agregada de ranking: total de pontos por usuário (US-11).
 */
public interface RankingProjection {

    Long getUsuarioId();

    Long getTotal();
}
