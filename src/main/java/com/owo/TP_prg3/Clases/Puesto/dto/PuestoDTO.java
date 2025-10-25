package com.owo.TP_prg3.Clases.Puesto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.owo.TP_prg3.Utilidades.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {
    @Column(order = 0, name = "ID Puesto")
    protected Long puestoId;

    @Column(order = 1, name = "Nombre")
    protected String nombre;

    @Column(order = 2, name = "Comisión")
    protected BigDecimal comision;

    @Column(order = 3, name = "ID Dueño")
    protected Long duenioId;
}
