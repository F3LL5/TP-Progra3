package com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPrecioHistorialDTO {
    protected Long id;
    protected Long item_id;
    protected Double precio_anterior;
    protected Double precio_nuevo;
    protected LocalDate fecha_cambio;
}
