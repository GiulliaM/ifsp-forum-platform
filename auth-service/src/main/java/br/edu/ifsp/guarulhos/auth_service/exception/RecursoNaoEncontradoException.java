package br.edu.ifsp.guarulhos.auth_service.exception;

/**
 * Lançada quando o usuário solicitado não existe (ex.: exclusão de conta); resulta em HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
