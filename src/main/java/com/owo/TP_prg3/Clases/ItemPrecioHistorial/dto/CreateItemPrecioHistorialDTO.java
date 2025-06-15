package com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemPrecioHistorialDTO {
    @NotBlank(message = "El id del item es obligatorio.")
    @Size(max = 100, message = "El id del item no debe pasar los 100 caracteres.")
    protected Long item_id;

    @NotBlank(message = "El precio anterior del item es obligatorio.")
    @Min(value= 0,message = "El precio anterios del item no puede ser 0.")
    protected Double precio_anterior;

    @NotBlank (message ="El precio nuevo del item es obligatorio.")
    @Min (value = 0,message = "El precio nuevo del item no puede ser 0.")
    protected Double precio_nuevo;

    @NotBlank (message = "La fecha de cambio es obligatoria.")
    @Size (max = 100,message = "La fecha de cambio no debe pasar los 100 caracteres.")
    protected LocalDate fecha_cambio;
}
