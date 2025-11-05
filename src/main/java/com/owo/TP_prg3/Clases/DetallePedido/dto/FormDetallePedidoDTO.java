package com.owo.TP_prg3.Clases.DetallePedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class FormDetallePedidoDTO {

    private Long productoId;

    private Integer cantidad;

}
