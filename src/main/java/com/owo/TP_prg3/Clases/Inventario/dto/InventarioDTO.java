package com.owo.TP_prg3.Clases.Inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {

    private Long inventario_id;

    private Integer cantidad;

    private Long producto_id;

    private Integer stockMin;

    private BigDecimal precioVenta;

    private BigDecimal costoAdquisicion;

}
