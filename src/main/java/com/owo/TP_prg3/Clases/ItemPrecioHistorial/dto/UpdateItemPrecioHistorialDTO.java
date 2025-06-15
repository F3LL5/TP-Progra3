package com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateItemPrecioHistorialDTO {
    @Size(max = 100,message = "El id del item no debe pasar los 100 caracteres.")
    protected Long item_id;

    @Min(value=0,message = "El precio anterior del item no puede ser 0.")
    protected Double precio_anterior;

    @Min(value=0,message = "El precio nuevo del item no puede ser 0.")
    protected Double precio_nuevo;

    @Size(max = 100,message = "La fecha del cambio no debe pasar los 100 caracteres.")
    protected LocalDate fecha_cambio;
}
