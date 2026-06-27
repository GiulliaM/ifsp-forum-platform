package br.edu.ifsp.guarulhos.gamification_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Conquista efetivamente desbloqueada por um usuário. A constraint única evita
 * desbloquear a mesma badge duas vezes (US-12, CA3).
 */
@Entity
@Table(
        name = "usuario_conquista",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuario_conquista",
                columnNames = {"usuario_id", "conquista_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioConquista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "conquista_id", nullable = false)
    private Long conquistaId;

    @Column(name = "desbloqueada_em", updatable = false)
    private LocalDateTime desbloqueadaEm;

    @PrePersist
    protected void onCreate(){
        this.desbloqueadaEm = LocalDateTime.now();
    }
}