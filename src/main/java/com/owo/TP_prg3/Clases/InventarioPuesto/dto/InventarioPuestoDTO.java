package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.owo.TP_prg3.Utilidades.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioPuestoDTO {
    @Column(order = 0, name = "ID")
    private Long inventario_id;

    @Column(order = 1, name = "Cantidad")
    private Integer cantidad;

    @Column(order = 2, name = "Puesto ID")
    private Long puestoId;

    @Column(order = 3, name = "Item ID")
    private Long itemId;

    @Column(order = 4, name = "Stock Mínimo")
    private Integer stockMin;

    @Column(order = 5, name = "Precio Venta")
    private BigDecimal precioVenta;

    @Column(order = 6, name = "Costo Adquisición")
    private BigDecimal costoAdquisicion;

}
