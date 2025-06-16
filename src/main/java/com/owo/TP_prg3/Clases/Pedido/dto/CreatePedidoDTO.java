package com.owo.TP_prg3.Clases.Pedido.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePedidoDTO {
    @NotNull(message = "El ID de la transacción es obligatorio.")
    protected Long transaccionId;
    @NotNull(message = "El ID del puesto es obligatorio.")
    protected Long puestoId;
}
