package com.owo.TP_prg3.Clases.User.dto;

import com.owo.TP_prg3.Clases.User.modelo.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;

    private RolUsuario rol;

    private Long entidadId;

    private Integer dni;

}
