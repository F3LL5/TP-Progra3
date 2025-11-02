package com.owo.TP_prg3.Clases.Persona.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "personas")
@Data @AllArgsConstructor @NoArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id", unique = true)
    protected Long personaId;

    @Column(nullable = false)
    protected String nombre;

    @Column(nullable = false)
    protected Integer edad;

    @Column(nullable = false, unique = true)
    protected Integer dni;
}
