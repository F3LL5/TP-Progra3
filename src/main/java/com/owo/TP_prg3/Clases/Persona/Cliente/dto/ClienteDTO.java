package com.owo.TP_prg3.Clases.Persona.Cliente.dto;

import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class ClienteDTO extends PersonaDTO {
    
    private Long clienteId; 

    public ClienteDTO(Long persona_id, Integer dni, String nombre, Integer edad, Long clienteId) {
        super(persona_id, dni, nombre, edad);
        this.clienteId = clienteId;
    }
}
