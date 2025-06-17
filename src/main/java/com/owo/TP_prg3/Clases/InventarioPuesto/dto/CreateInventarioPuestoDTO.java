package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventarioPuestoDTO {
    @NotNull
    @Positive(message = "La cantidad debe ser un número positivo")
    protected Integer cantidad;

    @NotNull
    @Positive(message = "El ID del puesto debe ser un número positivo")
    protected Long puestoId;

    @NotNull
    @Positive(message = "El ID del itemId debe ser un número positivo")
    protected Long itemId;

    @NotNull
    @Positive(message = "El stock mínimo debe ser un número positivo")
    protected Integer stockMin;

    @NotNull
    @Positive(message = "El precio de venta debe ser un número positivo")
    protected BigDecimal precioVenta;

    @NotNull
    @Positive(message = "El costo de adquisicion debe ser un número positivo")
    protected BigDecimal costoAdquisicion;
}
