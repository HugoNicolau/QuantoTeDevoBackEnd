package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNaoEncontradoException extends RuntimeException {
    
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(String.format("%s com ID %d não encontrado", recurso, id));
    }
}
