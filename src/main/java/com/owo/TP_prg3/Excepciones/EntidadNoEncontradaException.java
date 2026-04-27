package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class EntidadNoEncontradaException extends SistemaException {
    public EntidadNoEncontradaException(String entidad, Long id) {
        super(
            String.format("%s no encontrado/a con ID: %d", entidad, id),
            HttpStatus.NOT_FOUND
        );
    }

    public EntidadNoEncontradaException(String msg) {
        super(
            msg,
            HttpStatus.NOT_FOUND
        );
    }
    
    public EntidadNoEncontradaException(String entidad, String campo, Object valor) {
        super(
            String.format("%s no encontrado/a con %s: %s", entidad, campo, valor),
            HttpStatus.NOT_FOUND
        );
    }
}
