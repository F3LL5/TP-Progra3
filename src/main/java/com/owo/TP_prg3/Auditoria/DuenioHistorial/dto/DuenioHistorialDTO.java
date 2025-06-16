package com.owo.TP_prg3.Auditoria.DuenioHistorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenioHistorialDTO {
    protected Long id_historial_nuevo;
    protected Long puesto_id;
    protected Long duenio_anterior_id;
    protected Long duenio_nuevo_id;
    protected LocalDate fecha_cambio;
}
