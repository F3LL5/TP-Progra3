package com.owo.TP_prg3.Clases.DetallePedido.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDetallePedidoDTO {
    @NotNull(message = "El ID del pedido es obligatorio.")
    protected Long pedidoId;

    @NotNull(message = "El ID del item es obligatorio.")
    protected Long itemId;

    @NotNull(message = "La cantidad es obligatoria.")
    protected Integer cantidad;

}
