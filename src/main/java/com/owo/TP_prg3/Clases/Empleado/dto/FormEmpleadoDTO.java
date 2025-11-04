package com.owo.TP_prg3.Clases.Empleado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FormEmpleadoDTO {

    protected String nombre;

    protected Integer edad;

    protected Integer dni;

    protected String email;

    protected String contraseña;
}
