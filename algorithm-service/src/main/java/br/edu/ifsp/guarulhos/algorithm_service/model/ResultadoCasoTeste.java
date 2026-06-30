package br.edu.ifsp.guarulhos.algorithm_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resultados_caso_teste")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoCasoTeste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submissao_id", nullable = false)
    private Long submissaoId;

    @Column(name = "caso_teste_id", nullable = false)
    private Long casoTesteId;

    @Column(nullable = false)
    private boolean passou;

    @Column(columnDefinition = "TEXT")
    private String entrada;

    @Column(name = "saida_esperada", columnDefinition = "TEXT")
    private String saidaEsperada;

    @Column(name = "saida_obtida", columnDefinition = "TEXT")
    private String saidaObtida;
}
