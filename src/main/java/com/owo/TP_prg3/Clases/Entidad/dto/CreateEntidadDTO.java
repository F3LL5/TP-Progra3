package com.owo.TP_prg3.Clases.Entidad.dto;

import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "El rol es obligatorio.")
    @Size(max = 100, message = "El tipo de entidad no debe pasar los 100 caracteres.")
    protected RolEntidad rolEntidad;

    @NotBlank(message = "El tipo de entidad es obligatorio.")
    @Size(max = 100, message = "El rol no debe pasar los 100 caracteres.")
    protected String tipoEntidad;

    @NotNull(message = "La edad es obligatoria.")
    @Positive(message = "La edad de entidad debe ser un número positivo.")
    protected Integer edad;

    @NotNull(message = "El DNI es obligatorio.")
    @Positive(message = "El DNI debe ser un número positivo.")
    protected Integer dni;
}