package com.owo.TP_prg3.Clases.Producto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    protected Long producto_id;

    protected String nombre;

    protected String categoria;
}
