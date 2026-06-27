package br.edu.ifsp.guarulhos.gamification_service.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Catálogo de conquistas (badges) que o estudante pode desbloquear ao atingir marcos,
 * exibidas no perfil (US-12, CA3).
 */
@Entity
@Table(name = "conquista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conquista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 255)
    private String descricao;

    /** Identificador do critério avaliado pela regra de desbloqueio (ex.: PRIMEIRO_TOPICO, 10_EXERCICIOS). */
    @Column(nullable = false, length = 50)
    private String criterio;
}