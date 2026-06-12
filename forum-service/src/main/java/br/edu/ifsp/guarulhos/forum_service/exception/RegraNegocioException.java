package br.edu.ifsp.guarulhos.forum_service.exception;

// Lançada quando uma regra de negócio é violada (tópico encerrado, prazo de edição expirado,
// já segue o tópico...). Vira HTTP 409.
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem){
        super(mensagem);
    }
}
