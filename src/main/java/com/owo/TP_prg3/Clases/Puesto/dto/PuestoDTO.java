package com.owo.TP_prg3.Clases.Puesto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {
    protected Long puestoId;
    protected String nombre;
    protected Long duenioId;
}
