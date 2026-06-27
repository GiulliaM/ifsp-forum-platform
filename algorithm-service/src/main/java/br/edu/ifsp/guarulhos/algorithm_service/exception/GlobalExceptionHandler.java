package br.edu.ifsp.guarulhos.algorithm_service.exception;

import br.edu.ifsp.guarulhos.algorithm_service.dto.response.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Captura as exceções lançadas pelas services e pela validação dos DTOs, devolvendo uma
 * resposta JSON padronizada com o status HTTP correto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex){
        return montar(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(AcessoNegadoException ex){
        return montar(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException ex){
        return montar(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex){
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return montar(HttpStatus.BAD_REQUEST, mensagem);
    }

    private ResponseEntity<ErroResponse> montar(HttpStatus status, String mensagem){
        ErroResponse corpo = new ErroResponse(status.value(), mensagem, LocalDateTime.now());
        return ResponseEntity.status(status).body(corpo);
    }
}
