package com.owo.TP_prg3.Clases.CuentaBancaria.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCuentaBancariaDTO {
    @NotNull
    @Positive(message = "El ID de entidad debe ser un número positivo.")
    protected Long entidadId;

    @NotNull
    @Positive(message = "El saldo debe ser un número positivo.")
    protected BigDecimal saldo;
}
