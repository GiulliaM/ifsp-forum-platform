package br.edu.ifsp.guarulhos.auth_service.exception;

/**
 * Lançada no login quando o e-mail não existe ou a senha está incorreta; resulta em HTTP 401.
 * Mantém a mesma mensagem para os dois casos para não revelar qual deles falhou.
 */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException(String mensagem){
        super(mensagem);
    }
}
