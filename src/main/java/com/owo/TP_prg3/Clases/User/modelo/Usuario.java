package com.owo.TP_prg3.Clases.User.modelo;

import com.owo.TP_prg3.Clases.Persona.modelo.Persona;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El username será el DNI, que debe ser único.
    @Column(unique = true, nullable = false)
    private Integer dni;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    // Relación uno a uno: Un Usuario corresponde a una y solo una Entidad.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", referencedColumnName = "persona_id")
    private Persona entidad;
}