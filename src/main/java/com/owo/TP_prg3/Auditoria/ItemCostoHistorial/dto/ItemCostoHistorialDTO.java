package com.owo.TP_prg3.Auditoria.ItemCostoHistorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCostoHistorialDTO {
    protected Long id;
    protected Long item_id;
    protected Double costo_anterior;
    protected Double costo_nuevo;
    protected LocalDateTime fecha_cambio;
}
