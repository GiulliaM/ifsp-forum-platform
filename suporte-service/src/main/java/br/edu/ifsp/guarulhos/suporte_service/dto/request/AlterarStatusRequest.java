package br.edu.ifsp.guarulhos.suporte_service.dto.request;

import br.edu.ifsp.guarulhos.suporte_service.model.enums.StatusChamado;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlterarStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private StatusChamado status;
}
