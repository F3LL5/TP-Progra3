package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import com.owo.TP_prg3.Utilidades.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ItemStockDTO {

    @Column(order = 0, name = "ID Inventario")
    private Long inventario_id;

    @Column(order = 1, name = "Puesto ID")
    private Long puestoId;

    @Column(order = 2, name = "Código Ítem")
    protected Long itemId;

    @Column(order = 3, name = "Nombre")
    protected String nombre;

    @Column(order = 4, name = "Cantidad Actual")
    private Integer cantidad;

    @Column(order = 5, name = "Stock Mínimo")
    private Integer stockMin;

}
