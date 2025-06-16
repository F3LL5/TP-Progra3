package com.owo.TP_prg3.Clases.CuentaBancaria.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateCuentaBancariaDTO {
    @Positive(message = "El ID de entidad debe ser un número positivo.")
    protected Long entidadId;

    @Positive(message = "El costo debe ser un número positivo.")
    protected BigDecimal saldo;
}
