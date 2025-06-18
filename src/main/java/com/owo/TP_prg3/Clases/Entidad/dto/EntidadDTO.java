package com.owo.TP_prg3.Clases.Entidad.dto;

import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntidadDTO {
    @Column(order = 0, name = "ID")
    protected Long entidad_id;

    @Column(order = 1, name = "NOMBRE")
    protected String nombre;

    @Column(order = 2, name = "ROL")
    protected RolEntidad rolEntidad;

    @Column(order = 3, name = "TIPO ENTIDAD")
    protected String tipoEntidad;

    @Column(order = 4, name = "EDAD")
    protected Integer edad;

    @Column(order = 5, name = "DNI")
    protected Integer dni;
}