package com.owo.TP_prg3.Excepciones;

import org.springframework.http.HttpStatus;

public class StockInsuficienteException extends SistemaException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje, HttpStatus.BAD_REQUEST);
    }

}
