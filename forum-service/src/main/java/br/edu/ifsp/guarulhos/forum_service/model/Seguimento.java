package br.edu.ifsp.guarulhos.forum_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "seguimentos",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"usuario_id", "topico_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seguimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id", nullable = false)
    private Topico topico;

    @Column(name = "seguido_em", updatable = false)
    private LocalDateTime seguidoEm;

    @PrePersist
    protected void onCreate() {
        this.seguidoEm = LocalDateTime.now();
    }
}