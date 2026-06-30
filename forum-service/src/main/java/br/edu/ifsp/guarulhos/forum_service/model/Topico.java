package br.edu.ifsp.guarulhos.forum_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "topicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 400)
    private String categoria;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(nullable = false)
    private boolean fixado = false;

    @Column(nullable = false)
    private boolean encerrado = false;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "editado_em")
    private LocalDateTime editadoEm;

    @Column(name = "image_url")
    private String imageUrl;

    @PrePersist
    protected void onCreate(){
        this.criadoEm = LocalDateTime.now();
    }
}
