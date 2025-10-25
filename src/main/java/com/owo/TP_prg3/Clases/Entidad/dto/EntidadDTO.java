package com.owo.TP_prg3.Clases.Entidad.dto;

import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Utilidades.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntidadDTO {

    @Column(order = 0, name = "ID Entidad")
    protected Long entidad_id;

    @Column(order = 1, name = "DNI")
    protected Integer dni;

    @Column(order = 2, name = "Nombre")
    protected String nombre;

    @Column(order = 3, name = "Edad")
    protected Integer edad;

    @Column(order = 4, name = "Tipo Entidad")
    protected String tipoEntidad;

    @Column(order = 5, name = "Rol")
    protected RolEntidad rolEntidad;

}