package br.edu.ifsp.guarulhos.suporte_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faq_entrada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String pergunta;

    @Column(nullable = false, length = 1000)
    private String resposta;
}
