package com.owo.TP_prg3.Clases.DetallePedido.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateDetallePedidoDTO {
    @Positive(message = "El ID del pedido debe ser un número positivo.")
    protected Long pedidoId;

    @Positive(message = "El ID del itemId debe ser un número positivo.")
    protected Long itemId;

    @Positive(message = "La cantidad debe ser un número positivo")
    protected Integer cantidad;

    @Positive(message = "El precio total debe ser un número positivo")
    protected BigDecimal precioTotal;
}
