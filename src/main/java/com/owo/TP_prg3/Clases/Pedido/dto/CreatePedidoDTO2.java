package com.owo.TP_prg3.Clases.Pedido.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePedidoDTO2 {
    // hicimos otra variante porq lo vamos a ultilizar para crear una transaccion y un pedido al mismo tiempo automaticamente

    @NotNull(message = "El ID del puesto es obligatorio.")
    @Positive(message = "El ID del puesto debe ser un número positivo")
    protected Long puestoId;

    @NotNull(message = "El tipo de transaccion es obligatorio.")
    protected String tipoTransaccion;

    @NotNull(message = "El ID de la entidad es obligatorio.")
    @Positive(message = "El ID de entidad debe ser un número positivo.")
    protected Long idEntidad;

    @NotNull(message = "El ID de la cuenta de destino es obligatorio.")

    protected Long idCuentaDestino;
}