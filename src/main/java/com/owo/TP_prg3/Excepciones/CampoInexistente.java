package com.owo.TP_prg3.Excepciones;

public class CampoInexistente extends RuntimeException {
    public CampoInexistente(String message) {
        super(message);
    }
}
