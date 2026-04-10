package com.owo.TP_prg3.Clases.Duenio.dto;

import java.time.LocalDate;
import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class DuenioDTO extends PersonaDTO {

    private Long duenioId; 

    private String email;

    public DuenioDTO(Long persona_id, Long dni, String nombre, String apellido, LocalDate fechaNacimiento, Long duenioId, String email ) {
        super(persona_id, nombre, apellido, fechaNacimiento, dni);
        this.duenioId = duenioId;
        this.email = email;
    }
}
