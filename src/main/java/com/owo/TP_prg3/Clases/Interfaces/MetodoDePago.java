package com.owo.TP_prg3.Clases.Interfaces;

import java.math.BigDecimal;

public interface MetodoDePago {
    Long obtenerId();
    BigDecimal obtenerSaldo();
    void actualizarSaldo(BigDecimal nuevoSaldo);
    String obtenerTipo(); // Para identificar si es 'CUENTA_BANCARIA' o 'CAJA'
}
