package com.owo.TP_prg3.Clases.Puesto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePuestoDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no debe pasar los 100 caracteres.")
    protected String nombre;

    @NotNull(message = "El ID del dueño es obligatorio.")
    @Positive(message = "El ID del dueño debe ser un número positivo.")
    protected Long duenioId;
}
