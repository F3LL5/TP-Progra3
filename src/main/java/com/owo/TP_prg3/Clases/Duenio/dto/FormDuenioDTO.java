package com.owo.TP_prg3.Clases.Duenio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FormDuenioDTO {

    protected String nombre;

    protected Integer edad;

    protected Integer dni;

    protected String email;

    protected String contraseña;
}
