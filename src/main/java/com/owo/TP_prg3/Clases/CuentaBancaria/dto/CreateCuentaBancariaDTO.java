package com.owo.TP_prg3.Clases.CuentaBancaria.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCuentaBancariaDTO {
    @NotNull
    protected Long entidadId;

    @NotNull
    @Min(value = 0)
    protected BigDecimal saldo;
}
