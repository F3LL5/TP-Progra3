package com.owo.TP_prg3.Clases.Entidad.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEntidadDTO {
    @Size(max = 100, message = "El nombre no debe pasar los 100 caracteres.")
    protected String nombre;

    @Size(max = 100, message = "El tipo de entidad no debe pasar los 100 caracteres.")
    protected String tipoEntidad;

    @Size(max = 100, message = "El rol no debe pasar los 100 caracteres.")
    protected String rol;

    @Min(value = 0, message = "La edad no puede ser negativa.")
    protected Integer edad;

    @Min(value = 1, message = "El DNI debe ser un número positivo.")
    protected Integer dni;
}