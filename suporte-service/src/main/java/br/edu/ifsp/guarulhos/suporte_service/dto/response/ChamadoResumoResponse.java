package br.edu.ifsp.guarulhos.suporte_service.dto.response;

import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.TipoProblema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Item resumido para a listagem do estudante ("/me") e para o painel do moderador (US-17),
 * que inclui a flag "urgente" (sem resposta há mais de 48h).
 */
@Data
@Builder
public class ChamadoResumoResponse {
    private Long id;
    private String protocolo;
    private Long usuarioId;
    private TipoProblema tipoProblema;
    private StatusChamado status;
    private boolean urgente;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
