package com.owo.TP_prg3.Clases.Pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FacturaDTO {

    protected String cliente;

    protected String tipoTransaccion;

    protected String item;

    protected Integer cantidad;

    protected Double precioUnitario;

    protected BigDecimal subtotal;
}
