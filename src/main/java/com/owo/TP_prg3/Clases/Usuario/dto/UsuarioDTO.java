package com.owo.TP_prg3.Clases.Usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioDTO {

    private Long usuarioId;

    private String email;

    private String rol;

}
