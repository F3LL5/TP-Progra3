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
    @NotNull(message = "El ID del pedido es obligatorio.")
    protected Long pedidoId;

    @NotNull(message = "El ID del item es obligatorio.")
    protected Long itemId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    protected Integer cantidad;

    @NotNull(message = "El precio total es obligatorio.")
    @Min(value = 0, message = "El precio total no puede ser negativo.")
    protected BigDecimal precioTotal;
}
