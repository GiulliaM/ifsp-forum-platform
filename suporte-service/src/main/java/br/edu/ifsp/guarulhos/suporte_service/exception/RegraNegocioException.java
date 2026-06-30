package br.edu.ifsp.guarulhos.suporte_service.exception;

// Lançada quando uma regra de negócio é violada (ex.: responder chamado já encerrado).
// Vira HTTP 409.
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem){
        super(mensagem);
    }
}
