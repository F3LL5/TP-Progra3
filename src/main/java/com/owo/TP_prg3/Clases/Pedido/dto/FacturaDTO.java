package com.owo.TP_prg3.Clases.Pedido.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FacturaDTO {
    @Column(order = 0, name = "CLIENTE")
    protected String cliente;

    @Column(order = 1, name = "TIPO TRANSACCIÓN")
    protected String tipoTransaccion;

    @Column(order = 2, name = "ITEM")
    protected String item;

    @Column(order = 3, name = "CANTIDAD")
    protected Integer cantidad;

    @Column(order = 4, name = "PRECIO UNITARIO")
    protected Double precioUnitario;

    @Column(order = 5, name = "SUBTOTAL")
    protected BigDecimal subtotal;
}
