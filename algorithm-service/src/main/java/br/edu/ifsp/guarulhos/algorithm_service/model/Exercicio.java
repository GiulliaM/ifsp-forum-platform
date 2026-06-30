package br.edu.ifsp.guarulhos.algorithm_service.model;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Dificuldade;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.StatusExercicio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Dificuldade dificuldade;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(columnDefinition = "TEXT")
    private String restricoes;

    @Column(name = "exemplo_entrada", columnDefinition = "TEXT")
    private String exemploEntrada;

    @Column(name = "exemplo_saida", columnDefinition = "TEXT")
    private String exemploSaida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusExercicio status;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate(){
        this.criadoEm = LocalDateTime.now();
    }
}
