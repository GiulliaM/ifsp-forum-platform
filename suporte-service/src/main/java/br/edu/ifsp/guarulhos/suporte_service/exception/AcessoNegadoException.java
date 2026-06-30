package br.edu.ifsp.guarulhos.suporte_service.exception;

// Lançada quando o perfil do usuário não tem permissão para a ação (ex.: estudante
// tentando acessar o painel de moderador). Vira HTTP 403.
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem){
        super(mensagem);
    }
}
