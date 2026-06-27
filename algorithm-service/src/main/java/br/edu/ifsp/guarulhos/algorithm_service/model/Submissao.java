package br.edu.ifsp.guarulhos.algorithm_service.model;

import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Linguagem;
import br.edu.ifsp.guarulhos.algorithm_service.model.enums.Veredito;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Registro de uma submissão de solução, com código, veredito e métricas, que compõe
 * o histórico do estudante (US-08).
 */
@Entity
@Table(name = "submissoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exercicio_id", nullable = false)
    private Long exercicioId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Linguagem linguagem;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Veredito veredito;

    @Column(name = "tempo_execucao_ms")
    private long tempoExecucaoMs;

    @Column(name = "memoria_kb")
    private long memoriaKb;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate(){
        this.criadoEm = LocalDateTime.now();
    }
}
