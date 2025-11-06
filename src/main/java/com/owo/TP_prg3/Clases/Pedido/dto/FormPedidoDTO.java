package com.owo.TP_prg3.Clases.Pedido.dto;

import java.util.Set;
import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.modelo.TipoPedido;
import com.owo.TP_prg3.Clases.Transaccion.dto.FormTransaccionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormPedidoDTO {

    private TipoPedido tipo;

    private Long remitenteId;
    
    private Long destinatarioId;
    
    private FormTransaccionDTO transaccion; 

    private Set<FormDetallePedidoDTO> detalles;

}
