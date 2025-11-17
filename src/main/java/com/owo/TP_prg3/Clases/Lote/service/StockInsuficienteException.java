package com.owo.TP_prg3.Clases.Lote.service;

import org.springframework.http.HttpStatus;

import com.owo.TP_prg3.Excepciones.SistemaException;

public class StockInsuficienteException extends SistemaException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje, HttpStatus.BAD_REQUEST);
    }

}
