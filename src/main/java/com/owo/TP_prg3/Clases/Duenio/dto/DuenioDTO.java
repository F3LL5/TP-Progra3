package com.owo.TP_prg3.Clases.Duenio.dto;

import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class DuenioDTO extends PersonaDTO {

    private Long duenioId; 

    private String email;

    public DuenioDTO(Long persona_id, Integer dni, String nombre, Integer edad, Long duenioId, String email ) {
        super(persona_id, dni, nombre, edad);
        this.duenioId = duenioId;
        this.email = email;
    }
}
