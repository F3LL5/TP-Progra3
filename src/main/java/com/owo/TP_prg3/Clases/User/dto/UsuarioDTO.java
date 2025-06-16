package com.owo.TP_prg3.Clases.User.dto;

import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.User.modelo.RolUsuario;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    @Positive(message = "El ID debe ser un número positivo.")
    private Long id;

    @Positive(message = "El DNI debe ser un número positivo.")
    private Integer dni;

    @Size(max = 100, message = "El ROL no debe pasar de los 100 caracteres.")
    private RolUsuario rol;

    @Size(max = 100, message = "La ENTIDAD no debe pasar de los 100 caracteres.")
    private Entidad entidad;
}
