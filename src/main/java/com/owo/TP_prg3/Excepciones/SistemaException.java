package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class SistemaException extends RuntimeException {
    private final HttpStatus httpStatus;
    
    public SistemaException(String mensaje, HttpStatus httpStatus) {
        super(mensaje);
        this.httpStatus = httpStatus;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
