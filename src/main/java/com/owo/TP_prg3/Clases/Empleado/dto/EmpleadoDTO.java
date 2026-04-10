package com.owo.TP_prg3.Clases.Empleado.dto;

import java.time.LocalDate;

import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class EmpleadoDTO extends PersonaDTO {

    private Long empleadoId;

    private String email;

    public EmpleadoDTO(Long persona_id, Long dni, String nombre, String apellido, LocalDate fechaNacimiento, Long empleadoId, String email) {
        super(persona_id, nombre, apellido,fechaNacimiento, dni);
        this.empleadoId = empleadoId;
        this.email = email;
    }
}
