package br.edu.ifsp.guarulhos.suporte_service.dto.request;

import br.edu.ifsp.guarulhos.suporte_service.model.enums.TipoProblema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AbrirChamadoRequest {

    @NotNull(message = "Tipo de problema é obrigatório")
    private TipoProblema tipoProblema;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 20, message = "Descrição deve ter ao menos 20 caracteres")
    private String descricao;

    private String capturaTelaUrl;
}
