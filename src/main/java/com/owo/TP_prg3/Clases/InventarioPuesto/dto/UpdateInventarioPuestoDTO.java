package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventarioPuestoDTO {
    @Positive(message = "La cantidad debe ser un número positivo")
    protected Integer cantidad;

    @Positive(message = "El ID del puesto debe ser un número positivo")
    protected Long puestoId;

    @Positive(message = "El ID del item debe ser un número positivo")
    protected Long itemId;

    @Positive(message = "El stock mínimo debe ser un número positivo")
    protected Integer stockMin;

    @Positive(message = "El precio de venta debe ser un número positivo")
    protected BigDecimal precioVenta;
}
