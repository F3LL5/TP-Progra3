package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioPuestoDTO {
    protected Long inventario_id;
    protected Integer cantidad;
    protected Long puestoId;
    protected Long itemId;
    protected Integer stockMin;
    protected BigDecimal precioVenta;
    protected BigDecimal costoAdquisicion;
}
