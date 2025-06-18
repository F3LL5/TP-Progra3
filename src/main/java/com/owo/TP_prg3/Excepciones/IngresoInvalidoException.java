package com.owo.TP_prg3.Excepciones;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // return 400
public class IngresoInvalidoException extends RuntimeException {
    public IngresoInvalidoException(String message) {
        super(message);
    }
}
