package com.owo.TP_prg3.Clases.Pedido.dto;

import com.owo.TP_prg3.Utilidades.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    @Column(order = 0, name = "ID Pedido")
    protected Long pedidoId;

    @Column(order = 1, name = "ID Transacción")
    protected Long transaccionId;

    @Column(order = 2, name = "ID Puesto")
    protected Long puestoId;
}
