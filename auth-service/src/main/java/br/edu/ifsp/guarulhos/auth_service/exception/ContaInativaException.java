package br.edu.ifsp.guarulhos.auth_service.exception;

/**
 * Lançada no login quando a conta do usuário está inativa no sistema; resulta em HTTP 403.
 */
public class ContaInativaException extends RuntimeException {

    public ContaInativaException(String mensagem){
        super(mensagem);
    }
}
