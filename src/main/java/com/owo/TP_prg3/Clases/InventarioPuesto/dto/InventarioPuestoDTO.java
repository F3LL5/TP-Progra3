package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioPuestoDTO {

    private Long inventario_id;

    private Integer cantidad;

    private Long puestoId;

    private Long itemId;

    private Integer stockMin;

    private BigDecimal precioVenta;

    private BigDecimal costoAdquisicion;

}
