package br.edu.ifsp.guarulhos.algorithm_service.exception;

/**
 * Lançada quando uma regra de negócio é violada (exercício sem casos de teste, exercício
 * ainda em rascunho); resulta em HTTP 409.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem){
        super(mensagem);
    }
}
