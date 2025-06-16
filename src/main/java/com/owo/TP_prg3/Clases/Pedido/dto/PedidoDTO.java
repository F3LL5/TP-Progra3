package com.owo.TP_prg3.Clases.Pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    protected Long pedidoId;
    protected Long transaccionId;
}
