package br.edu.ifsp.guarulhos.suporte_service.exception;

// Lançada quando um chamado/recurso pesquisado não existe. Vira HTTP 404.
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
