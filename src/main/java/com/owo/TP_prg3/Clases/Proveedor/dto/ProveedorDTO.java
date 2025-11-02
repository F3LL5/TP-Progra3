package com.owo.TP_prg3.Clases.Proveedor.dto;

import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class ProveedorDTO extends PersonaDTO {
    
    private Long proveedorId; 

    public ProveedorDTO(Long persona_id, Integer dni, String nombre, Integer edad, Long clienteId) {
        super(persona_id, dni, nombre, edad);
        this.proveedorId = clienteId;
    }
}
