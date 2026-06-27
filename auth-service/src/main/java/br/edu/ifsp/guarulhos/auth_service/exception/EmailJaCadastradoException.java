package br.edu.ifsp.guarulhos.auth_service.exception;

/**
 * Lançada no cadastro quando o e-mail informado já pertence a outro usuário; resulta em HTTP 409.
 */
public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String mensagem){
        super(mensagem);
    }
}
