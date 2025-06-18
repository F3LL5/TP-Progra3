package com.owo.TP_prg3.Clases.Item.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    @Column(order = 0, name = "ID")
    protected Long item_id;

    @Column(order = 1, name = "NOMBRE")
    protected String nombre;

    @Column(order = 2, name = "CATEGORÍA")
    protected String categoria;
}
