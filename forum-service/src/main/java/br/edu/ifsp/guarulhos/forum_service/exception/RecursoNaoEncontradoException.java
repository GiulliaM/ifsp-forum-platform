package br.edu.ifsp.guarulhos.forum_service.exception;

// Lançada quando o recurso pedido não existe (tópico, comentário...). Vira HTTP 404.
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
