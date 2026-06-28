package br.edu.ifsp.guarulhos.forum_service.model;

import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoPontuacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pontuacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pontuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private int pontos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPontuacao tipo;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    // só preenchido no caso de RECEBER_LIKE: quem curtiu
    @Column(name = "curtidor_id")
    private Long curtidorId;

    @Column(name = "ganho_em", updatable = false)
    private LocalDateTime ganhoEm;

    @PrePersist
    protected void onCreate() {
        this.ganhoEm = LocalDateTime.now();
    }
}
