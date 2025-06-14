package com.owo.TP_prg3.Clases.User.dto;

import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.User.modelo.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private Integer dni;
    private RolUsuario rol;
    private Entidad entidad;
}
