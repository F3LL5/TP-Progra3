package com.owo.TP_prg3.Clases.Persona.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data @AllArgsConstructor
public class PersonaDTO {

    protected Long personaId;

    protected String nombre;

    protected String apellido;

    protected LocalDate fechaNacimiento;

    protected Long dni;

}