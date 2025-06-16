package com.owo.TP_prg3.Auditoria.DuenioHistorial.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="historial_cambio_duenio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuenioHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id",unique = true)
    protected Long id_historial_duenio;

    @Column (nullable = false)
    protected Long puesto_id;

    @Column (nullable = false)
    protected Long duenio_anterior_id;

    @Column (nullable = false)
    protected Long duenio_nuevo_id;

    @Column (nullable = false)
    protected LocalDate fecha_cambio;

}
