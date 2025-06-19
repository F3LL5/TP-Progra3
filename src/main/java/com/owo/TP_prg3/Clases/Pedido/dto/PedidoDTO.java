package com.owo.TP_prg3.Clases.Pedido.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    @Column(order = 0, name = "ID")
    protected Long pedidoId;

    @Column(order = 1, name = "TRANSACCION ID")
    protected Long transaccionId;

    @Column(order = 2, name = "PUESTO ID")
    protected Long puestoId;
}
