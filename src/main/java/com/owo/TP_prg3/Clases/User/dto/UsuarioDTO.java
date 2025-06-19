package com.owo.TP_prg3.Clases.User.dto;

import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.User.modelo.RolUsuario;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private Integer dni;
    private RolUsuario rol;
    private Long entidadId;

}
