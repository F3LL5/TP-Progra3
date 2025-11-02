package com.owo.TP_prg3.Clases.Puesto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePuestoDTO {
    @Size(max = 100, message = "El nombre no debe pasar los 100 caracteres.")
    protected String nombre;

    @Min(value = 0, message = "La comisión debe ser un número positivo o 0.")
    protected Long duenioId;

    @Positive(message = "La comisión debe ser un número positivo.")
    protected BigDecimal comision;
}
