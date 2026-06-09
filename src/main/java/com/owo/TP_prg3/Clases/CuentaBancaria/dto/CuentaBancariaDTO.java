package com.owo.TP_prg3.Clases.CuentaBancaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class CuentaBancariaDTO {

    protected Long cuentaBancariaId;
    
    protected String nombreBanco;

    private String cbu;

    protected BigDecimal saldo;
}
