package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class ReglaNegocioException extends SistemaException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
