package com.owo.TP_prg3.Clases.Cliente.dto;

import java.time.LocalDate;

import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class ClienteDTO extends PersonaDTO {
    
    private Long clienteId; 

    public ClienteDTO(Long persona_id, Long dni, String nombre, String apellido, LocalDate fechaNacimiento,Long clienteId) {
        super(persona_id, nombre, apellido,fechaNacimiento, dni);
        this.clienteId = clienteId;
    }
}
