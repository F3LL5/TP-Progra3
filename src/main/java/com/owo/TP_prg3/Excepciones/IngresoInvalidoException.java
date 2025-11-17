package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class IngresoInvalidoException extends SistemaException {

    public IngresoInvalidoException(String mensaje) {
        super(mensaje, HttpStatus.BAD_REQUEST);
    }
    
    public IngresoInvalidoException(String campo, String razon) {
        super(
            String.format("Campo '%s' inválido: %s", campo, razon),
            HttpStatus.BAD_REQUEST
        );
    }

}
