package br.edu.ifsp.guarulhos.auth_service.model;

import br.edu.ifsp.guarulhos.auth_service.model.enums.NivelAprendizado;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preferencias_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaUsuario {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel")
    private NivelAprendizado nivel;

    @ElementCollection
    @CollectionTable(name = "usuario_interesses", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "interesse")
    @Builder.Default
    private List<String> interesses = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "usuario_linguagens", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "linguagem")
    @Builder.Default
    private List<String> linguagens = new ArrayList<>();
}
