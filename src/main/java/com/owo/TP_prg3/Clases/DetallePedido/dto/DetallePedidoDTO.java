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
    @Column(order = 0, name = "ID Detalle Pedido")
    protected Long detallePedidoId;

    @Column(order = 1, name = "ID Pedido")
    protected Long pedidoId;

    @Column(order = 2, name = "Código Ítem")
    protected Long itemId;

    @Column(order = 3, name = "Cantidad")
    protected Integer cantidad;

    @Column(order = 4, name = "Subtotal")
    protected BigDecimal precioTotal;
}
