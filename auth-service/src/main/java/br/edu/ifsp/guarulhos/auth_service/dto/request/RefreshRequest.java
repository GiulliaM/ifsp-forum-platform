package br.edu.ifsp.guarulhos.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotBlank(message = "refreshToken é obrigatório")
    private String refreshToken;
}
