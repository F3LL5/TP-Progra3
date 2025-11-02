package com.owo.TP_prg3.Clases.Producto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ProductoDTO {

    protected Long producto_id;

    protected String nombre;

    protected String categoria;
}
