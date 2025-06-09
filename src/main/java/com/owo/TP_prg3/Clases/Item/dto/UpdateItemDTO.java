package com.owo.TP_prg3.Clases.Item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDTO {

    @Size(max = 100, message = "El nombre no debe pasar los 100 caracteres.")
    protected String nombre;

    @Size(max = 100, message = "La categoria no debe pasar los 100 caracteres.")
    protected String categoria;

    @Min(value = 0, message = "El costo no puede ser negativo.")
    protected Double costo;
}
