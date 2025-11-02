package com.owo.TP_prg3.Clases.Persona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data @AllArgsConstructor
public class PersonaDTO {

    protected Long persona_id;

    protected Integer dni;

    protected String nombre;

    protected Integer edad;

}