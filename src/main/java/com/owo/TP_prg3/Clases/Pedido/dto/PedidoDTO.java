package com.owo.TP_prg3.Clases.Pedido.dto;

import java.util.Set;
import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.Enum.TipoPedido;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long pedidoId;
    private TransaccionDTO transaccion;
    private TipoPedido tipo;
    private Set<FormDetallePedidoDTO> detalles;
}
