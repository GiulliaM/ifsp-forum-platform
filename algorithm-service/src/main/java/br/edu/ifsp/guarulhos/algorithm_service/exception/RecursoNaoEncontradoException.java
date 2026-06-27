package br.edu.ifsp.guarulhos.algorithm_service.exception;

/**
 * Lançada quando o recurso solicitado não existe (exercício, submissão); resulta em HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
