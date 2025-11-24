package com.owo.TP_prg3.Clases.DetallePedido.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class DetallePedidoDTO {

    protected Long detallePedidoId;

    protected Long producto_id;

    protected String producto_nombre;

    protected Integer cantidad;

    protected BigDecimal subtotal;

    private BigDecimal costoUnitario;
}
