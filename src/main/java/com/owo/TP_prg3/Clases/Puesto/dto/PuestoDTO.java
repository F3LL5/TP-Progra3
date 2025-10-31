package com.owo.TP_prg3.Clases.Puesto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {

    protected Long puestoId;

    protected String nombre;

    protected BigDecimal comision;

    protected Long duenioId;
}
