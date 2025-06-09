package com.owo.TP_prg3.Clases.Entidad.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntidadDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no debe pasar los 100 caracteres.")
    protected String nombre;

    @NotBlank(message = "El tipo de entidad es obligatorio.")
    @Size(max = 100, message = "El tipo de entidad no debe pasar los 100 caracteres.")
    protected String tipoEntidad;

    @NotBlank(message = "El rol es obligatorio.")
    @Size(max = 100, message = "El rol no debe pasar los 100 caracteres.")
    protected String rol;

    @NotNull(message = "La edad es obligatoria.")
    @Min(value = 0, message = "La edad no puede ser negativa.")
    protected Integer edad;

    @NotNull(message = "El DNI es obligatorio.")
    @Min(value = 1, message = "El DNI debe ser un número positivo.")
    protected Integer dni;
}