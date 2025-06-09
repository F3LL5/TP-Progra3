package com.owo.TP_prg3.Clases.DetallePedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDetallePedidoDTO {
    @NotNull
    protected Long itemId;

    @NotNull
    @Min(value = 1)
    protected Integer cantidad;

    @NotNull
    @Min(value = 0)
    protected BigDecimal precioTotal;
}
