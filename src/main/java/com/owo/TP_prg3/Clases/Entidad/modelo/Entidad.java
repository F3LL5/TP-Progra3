package com.owo.TP_prg3.Clases.Entidad.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entidades") // Nombre de la tabla en la base de datos
@Data @AllArgsConstructor @NoArgsConstructor // Anotaciones de Lombok
public class Entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entidad_id", unique = true)
    protected Long entidad_id;

    @Column(nullable = false)
    protected String nombre;

    @Column(name = "tipo_entidad", nullable = false)
    protected String tipoEntidad;

    @Column(nullable = false)
    protected String rol;

    @Column(nullable = false)
    protected Integer edad;

    @Column(nullable = false, unique = true)
    protected Integer dni;
}
