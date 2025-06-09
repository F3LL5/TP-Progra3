package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventarioPuestoDTO {
    @NotNull
    @Min(value = 0)
    protected Integer cantidad;

    @NotNull
    protected Long puestoId;

    @NotNull
    protected Long itemId;

    @NotNull
    @Min(value = 0)
    protected Integer stockMin;

    @NotNull
    @Min(value = 0)
    protected BigDecimal precioVenta;
}
