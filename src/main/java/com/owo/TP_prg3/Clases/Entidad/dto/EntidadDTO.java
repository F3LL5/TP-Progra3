package com.owo.TP_prg3.Clases.Entidad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntidadDTO {
    protected Long entidad_id;
    protected String nombre;
    protected String tipoEntidad;
    protected String rol;
    protected Integer edad;
    protected Integer dni;
}