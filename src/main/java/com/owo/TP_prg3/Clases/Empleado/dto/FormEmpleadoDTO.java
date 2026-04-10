package com.owo.TP_prg3.Clases.Empleado.dto;

import java.time.LocalDate;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class FormEmpleadoDTO extends FormPersonaDTO {

    private Long empleadoId;

    private String email;

    private String contraseña;

    public FormEmpleadoDTO(Long dni, String nombre, String apellido, LocalDate fechaNacimiento, Long empleadoId, String email) {
        super(nombre, apellido,fechaNacimiento, dni);
        this.empleadoId = empleadoId;
        this.email = email;
    }
}
