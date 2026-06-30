package br.edu.ifsp.guarulhos.personalization_service.exception;

import br.edu.ifsp.guarulhos.personalization_service.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErroResponse> tratarHeaderAusente(MissingRequestHeaderException ex) {
        return montar(HttpStatus.BAD_REQUEST, "Cabeçalho obrigatório ausente: " + ex.getHeaderName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception ex) {
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado. Tente novamente mais tarde.");
    }

    private ResponseEntity<ErroResponse> montar(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ErroResponse(status.value(), mensagem, LocalDateTime.now()));
    }
}
