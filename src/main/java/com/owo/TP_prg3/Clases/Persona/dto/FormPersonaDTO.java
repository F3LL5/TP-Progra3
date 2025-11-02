package com.owo.TP_prg3.Clases.Persona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FormPersonaDTO {

    protected String nombre;

    protected String tipoEntidad;

    protected Integer edad;

    protected Integer dni;
}