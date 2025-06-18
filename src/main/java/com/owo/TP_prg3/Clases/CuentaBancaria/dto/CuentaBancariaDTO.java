package com.owo.TP_prg3.Clases.CuentaBancaria.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancariaDTO {
    @Column(order = 0, name = "ID")
    protected Long cuentaBancariaId;

    @Column(order = 1, name = "ID ENTIDAD")
    protected Long entidadId;

    @Column(order = 3, name = "SALDO")
    protected BigDecimal saldo;
}
