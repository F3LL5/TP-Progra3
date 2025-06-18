package com.owo.TP_prg3.Clases.Puesto.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {
    @Column(order = 0, name = "ID")
    protected Long puestoId;

    @Column(order = 1, name = "NOMBRE")
    protected String nombre;

    @Column(order = 2, name = "COMISION")
    protected BigDecimal comision;

    @Column(order = 3, name = "ID DUEÑO")
    protected Long duenioId;
}
