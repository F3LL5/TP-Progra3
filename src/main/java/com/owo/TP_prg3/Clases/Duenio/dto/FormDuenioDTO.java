package com.owo.TP_prg3.Clases.Duenio.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FormDuenioDTO {

    protected String nombre;

    protected String apellido;

    protected Long dni;
    
    protected LocalDate fechaNacimiento;

    protected String email;

    protected String contraseña;
}
