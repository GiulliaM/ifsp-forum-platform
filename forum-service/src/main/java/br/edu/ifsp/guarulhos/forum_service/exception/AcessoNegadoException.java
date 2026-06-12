package br.edu.ifsp.guarulhos.forum_service.exception;

// Lançada quando o usuário não tem permissão para a ação (não é moderador, não é o dono). Vira HTTP 403.
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem){
        super(mensagem);
    }
}
