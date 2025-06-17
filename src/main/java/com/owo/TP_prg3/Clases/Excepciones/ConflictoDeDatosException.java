package com.owo.TP_prg3.Clases.Excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // return 409
public class ConflictoDeDatosException extends RuntimeException {
    public ConflictoDeDatosException(String message) {
        super(message);
    }
}
