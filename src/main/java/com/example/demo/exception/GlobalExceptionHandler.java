package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException e) {
        ErrorResponse erro = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Recurso não encontrado")
            .message(e.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }
    
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponse> tratarNegocio(NegocioException e) {
        ErrorResponse erro = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erro de negócio")
            .message(e.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> tratarValidacao(MethodArgumentNotValidException e) {
        Map<String, String> erros = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(erro -> 
            erros.put(erro.getField(), erro.getDefaultMessage()));
        
        ErrorResponse erro = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erro de validação")
            .message("Dados inválidos")
            .validationErrors(erros)
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarErroGeral(Exception e) {
        ErrorResponse erro = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Erro interno do servidor")
            .message("Ocorreu um erro inesperado")
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
