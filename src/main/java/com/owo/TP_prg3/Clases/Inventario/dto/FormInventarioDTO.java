package com.owo.TP_prg3.Clases.Inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormInventarioDTO {

    protected Integer cantidad;

    protected Long producto_id;

    protected Integer stockMin;

    protected BigDecimal precioVenta;

    protected BigDecimal costoAdquisicion;

    //Constructor default para productos
    public FormInventarioDTO(Long producto_id){
        this.producto_id = producto_id;
        cantidad = 0;
        stockMin = 0;
        precioVenta = BigDecimal.ZERO;
        costoAdquisicion = BigDecimal.ZERO;
    }
}
