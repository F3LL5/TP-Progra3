package com.owo.TP_prg3.Clases.Usuario.dto;

import com.owo.TP_prg3.Clases.Enum.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class FormUsuarioDTO {

    private String email;

    private String contraseña;

    RolUsuario rol;
}
