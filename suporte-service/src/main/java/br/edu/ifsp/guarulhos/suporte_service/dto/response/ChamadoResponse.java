package br.edu.ifsp.guarulhos.suporte_service.dto.response;

import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import br.edu.ifsp.guarulhos.suporte_service.model.enums.TipoProblema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChamadoResponse {
    private Long id;
    private String protocolo;
    private Long usuarioId;
    private TipoProblema tipoProblema;
    private String descricao;
    private String capturaTelaUrl;
    private StatusChamado status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private List<RespostaResponse> respostas;
}
