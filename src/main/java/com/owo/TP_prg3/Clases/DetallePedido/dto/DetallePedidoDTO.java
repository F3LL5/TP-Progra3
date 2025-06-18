package com.owo.TP_prg3.Clases.DetallePedido.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {
    @Column(order = 0, name = "ID")
    protected Long detallePedidoId;

    @Column(order = 1, name = "PEDIDO")
    protected Long pedidoId;

    @Column(order = 2, name = "ITEM")
    protected Long itemId;

    @Column(order = 3, name = "CANTIDAD")
    protected Integer cantidad;

    @Column(order = 4, name = "PRECIO TOTAL")
    protected BigDecimal precioTotal;
}
