package com.owo.TP_prg3.Clases.DetallePedido.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDetallePedidoDTO {

    protected Long pedidoId;

    protected Long itemId;

    @Min(value = 0)
    protected Integer cantidad;

    protected BigDecimal precioTotal;
}
