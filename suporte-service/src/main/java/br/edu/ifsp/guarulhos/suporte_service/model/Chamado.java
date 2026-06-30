package br.edu.ifsp.guarulhos.suporte_service.model;

import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.TipoProblema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * O protocolo só existe após o primeiro save (depende do id gerado pelo banco), por isso
 * é gravado em uma segunda escrita pelo service.
 */
@Entity
@Table(name = "chamado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String protocolo;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_problema", nullable = false, length = 20)
    private TipoProblema tipoProblema;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(name = "captura_tela_url")
    private String capturaTelaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusChamado status;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate(){
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
        if (this.status == null){
            this.status = StatusChamado.ABERTO;
        }
    }
}
