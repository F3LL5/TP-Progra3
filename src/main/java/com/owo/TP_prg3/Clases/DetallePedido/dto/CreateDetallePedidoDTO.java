package com.owo.TP_prg3.Clases.DetallePedido.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDetallePedidoDTO {
    @NotNull(message = "El ID del pedido es obligatorio.")
    @Positive(message = "El ID del pedido debe ser un número positivo.")
    protected Long pedidoId;

    @NotNull(message = "El ID del itemId es obligatorio.")
    @Positive(message = "El ID del itemId debe ser un número positivo.")
    protected Long itemId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser un número positivo")
    protected Integer cantidad;

}
