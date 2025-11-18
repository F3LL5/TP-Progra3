package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class CampoRequeridoException extends SistemaException {
    public CampoRequeridoException(String campo) {
        super(
            String.format("El campo '%s' es obligatorio y no puede estar vacío", campo),
            HttpStatus.BAD_REQUEST
        );
    }
    
}
