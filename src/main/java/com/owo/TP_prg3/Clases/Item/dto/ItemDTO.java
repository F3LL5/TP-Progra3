package com.owo.TP_prg3.Clases.Item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    protected Long item_id;
    protected String nombre;
    protected String categoria;
    protected Double costo;

}
