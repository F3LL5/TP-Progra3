package com.owo.TP_prg3.Clases.Pedido.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePedidoDTO {
    @NotNull(message = "El ID de la transacción es obligatorio.")
    @Positive(message = "El ID de transacción debe ser un número positivo")
    protected Long transaccionId;
    @NotNull(message = "El ID del puesto es obligatorio.")
    @Positive(message = "El ID del puesto debe ser un número positivo")
    protected Long puestoId;
}
