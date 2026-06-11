package br.edu.ifsp.guarulhos.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthResponse {

    private String token;
    private String nome;
    private String email;
    private String perfil;
}
