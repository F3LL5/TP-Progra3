package com.owo.TP_prg3.Clases.InventarioPuesto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ItemStockDTO {

    private Long inventario_id;

    private Long puestoId;

    protected Long itemId;

    protected String nombre;

    private Integer cantidad;

    private Integer stockMin;

}
