package br.edu.ifsp.guarulhos.algorithm_service.exception;

/**
 * Lançada quando o usuário não tem permissão para a ação (não é moderador, não é o dono);
 * resulta em HTTP 403.
 */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem){
        super(mensagem);
    }
}
