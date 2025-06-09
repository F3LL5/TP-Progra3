package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventarioPuestoDTO {
    @Min(value = 0)
    protected Integer cantidad;

    protected Long puestoId;

    protected Long itemId;

    @Min(value = 0)
    protected Integer stockMin;

    @Min(value = 0)
    protected BigDecimal precioVenta;
}
