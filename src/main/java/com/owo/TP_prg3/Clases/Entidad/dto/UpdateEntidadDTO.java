package com.owo.TP_prg3.Clases.Entidad.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEntidadDTO {
    @Size(max = 100, message = "El nombre no debe pasar los 100 caracteres.")
    protected String nombre;

    @Size(max = 100, message = "El tipo de entidad no debe pasar los 100 caracteres.")
    protected RolEntidad rolEntidad;

    @Size(max = 100, message = "El rol no debe pasar los 100 caracteres.")
    protected String tipoEntidad;

    @Positive(message = "La edad de entidad debe ser un número positivo.")
    protected Integer edad;

    @Positive(message = "El DNI de entidad debe ser un número positivo.")
    protected Integer dni;
}