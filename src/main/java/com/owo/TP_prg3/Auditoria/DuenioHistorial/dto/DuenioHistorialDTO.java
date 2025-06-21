package com.owo.TP_prg3.Auditoria.DuenioHistorial.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenioHistorialDTO {

    @Column(order = 0, name = "ID Historial")
    protected Long id_historial_nuevo;

    @Column(order = 1, name = "ID Puesto")
    protected Long puesto_id;

    @Column(order = 2, name = "ID Dueño Antiguo")
    protected Long duenio_anterior_id;

    @Column(order = 3, name = "ID Dueño Nuevo")
    protected Long duenio_nuevo_id;

    @Column(order = 4, name = "Fecha del Cambio")
    protected String fecha_cambio;

}
