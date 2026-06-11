package br.edu.ifsp.guarulhos.forum_service.model;

import br.edu.ifsp.guarulhos.forum_service.model.enums.TipoLike;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"usuario_id", "tipo", "referencia_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLike tipo;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;
}
