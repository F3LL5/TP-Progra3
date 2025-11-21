package com.owo.TP_prg3.Clases.Pedido.dto;

import com.owo.TP_prg3.Clases.Enum.TipoPedido;
import com.owo.TP_prg3.Clases.Transaccion.dto.FormTransaccionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormPedidoDTO {

    private TipoPedido tipo;
    
    private FormTransaccionDTO transaccion; 

}
