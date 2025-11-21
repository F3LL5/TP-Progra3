package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class EntidadDuplicadaException extends SistemaException {
    public EntidadDuplicadaException(String entidad, String campo, Object valor) {
        super(
            String.format("%s duplicado: Ya existe con %s = '%s'", entidad, campo, valor),
            HttpStatus.CONFLICT
        );
    }
    
    public EntidadDuplicadaException(String entidad, String detalle) {
        super(
            String.format("%s duplicado: %s", entidad, detalle),
            HttpStatus.CONFLICT
        );
    }
}
