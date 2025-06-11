package com.owo.TP_prg3.Clases.Pedido.dto;

import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    protected Long pedidoId;
    protected Long transaccionId;
    protected List<DetallePedidoDTO> detallesPedido; // Opcional: para incluir los detalles al obtener un pedido
}
