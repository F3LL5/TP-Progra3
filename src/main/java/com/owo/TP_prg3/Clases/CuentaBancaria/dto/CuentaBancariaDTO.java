package com.owo.TP_prg3.Clases.CuentaBancaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.owo.TP_prg3.Utilidades.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancariaDTO {
    @Column(order = 0, name = "ID Cuenta Bancaria")
    protected Long cuentaBancariaId;

    @Column(order = 1, name = "ID del Dueño")
    protected Long entidadId;

    @Column(order = 3, name = "Saldo actual")
    protected BigDecimal saldo;
}
