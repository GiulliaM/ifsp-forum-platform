package br.edu.ifsp.guarulhos.suporte_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResponderChamadoRequest {

    @NotBlank(message = "Mensagem é obrigatória")
    private String mensagem;
}
