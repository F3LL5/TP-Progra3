package com.owo.TP_prg3.Clases.DetallePedido.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class FormDetallePedidoDTO {

    private Long productoId;

    private Integer cantidad;

    //Para pedidos de tipo compra al proveedor. Null si es una venta
    private BigDecimal costoUnitario;

}
