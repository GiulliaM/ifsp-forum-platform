package br.edu.ifsp.guarulhos.gamification_service.exception;

/**
 * Lançada quando o recurso solicitado não existe; resulta em HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
