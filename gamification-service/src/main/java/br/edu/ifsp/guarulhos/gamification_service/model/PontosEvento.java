package br.edu.ifsp.guarulhos.gamification_service.model;

import br.edu.ifsp.guarulhos.gamification_service.model.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registro de um evento pontuável já valorado. O total de pontos de um usuário é a soma
 * de seus eventos (US-12). A constraint única garante idempotência: o mesmo fato
 * (tipo + referência + usuário) não pontua duas vezes.
 */
@Entity
@Table(
        name = "pontos_evento",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_evento_idempotente",
                columnNames = {"tipo", "referencia_id", "usuario_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontosEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEvento tipo;

    /** Usuário que recebe os pontos (ex.: autor que recebeu o like). */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    /** Id do tópico/comentário/exercício que originou o evento. */
    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(nullable = false)
    private int pontos;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate(){
        this.criadoEm = LocalDateTime.now();
    }
}
