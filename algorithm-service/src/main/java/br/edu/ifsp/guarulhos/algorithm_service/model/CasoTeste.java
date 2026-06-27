package br.edu.ifsp.guarulhos.algorithm_service.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Caso de teste de um exercício, cuja saída esperada é comparada com a saída enviada
 * pelo estudante durante a avaliação da submissão (US-08 e US-10).
 */
@Entity
@Table(name = "casos_teste")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CasoTeste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exercicio_id", nullable = false)
    private Long exercicioId;

    @Column(columnDefinition = "TEXT")
    private String entrada;

    @Column(name = "saida_esperada", nullable = false, columnDefinition = "TEXT")
    private String saidaEsperada;

    @Column(nullable = false)
    private boolean oculto = true;
}
