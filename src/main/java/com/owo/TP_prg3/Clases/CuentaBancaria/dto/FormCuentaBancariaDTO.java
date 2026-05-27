package com.owo.TP_prg3.Clases.CuentaBancaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class FormCuentaBancariaDTO {

    private String nombre_banco;

    private Integer cbu;

    protected BigDecimal saldo;

}

