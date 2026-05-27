package com.owo.TP_prg3.Clases.Producto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormProductoDTO {

    private String nombre;

    private String categoria;

    private String url;

    private Integer stockMin;

    private BigDecimal precioVenta;

}
