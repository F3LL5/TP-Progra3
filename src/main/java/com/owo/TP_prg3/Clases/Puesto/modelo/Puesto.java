package com.owo.TP_prg3.Clases.Puesto.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;

@Entity
@Table(name = "puestos")
@Data @AllArgsConstructor @NoArgsConstructor
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "puesto_id")
    protected Long puestoId;

    @Column(nullable = false, length = 100)
    protected String nombre;

    @ManyToOne
    @JoinColumn(name = "duenio_id", referencedColumnName = "entidad_id", nullable = false)
    protected Entidad duenio;

}
