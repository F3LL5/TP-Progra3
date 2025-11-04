package com.owo.TP_prg3.Clases.Empleado.dto;

import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class EmpleadoDTO extends PersonaDTO {

    private Long empleadoId;

    private String email;

    public EmpleadoDTO(Long persona_id, Integer dni, String nombre, Integer edad, Long empleadoId, String email) {
        super(persona_id, dni, nombre, edad);
        this.empleadoId = empleadoId;
        this.email = email;
    }
}
