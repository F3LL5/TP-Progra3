package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class OperacionNoPermitidaException extends SistemaException {
    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje, HttpStatus.FORBIDDEN);
    }
}
