package br.edu.ifsp.guarulhos.auth_service.exception;

public class RefreshTokenInvalidoException extends RuntimeException {
    public RefreshTokenInvalidoException(String mensagem) {
        super(mensagem);
    }
}
