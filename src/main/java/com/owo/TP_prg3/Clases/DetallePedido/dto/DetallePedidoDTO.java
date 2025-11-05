package com.owo.TP_prg3.Clases.DetallePedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import com.owo.TP_prg3.Clases.Producto.dto.ProductoDTO;

@Data @NoArgsConstructor @AllArgsConstructor
public class DetallePedidoDTO {

    protected Long detallePedidoId;

    protected ProductoDTO productoDTO;

    protected Integer cantidad;

    protected BigDecimal subtotal;
}
