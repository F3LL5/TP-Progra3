package com.owo.TP_prg3.Auntenticacion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UsuarioAutenticado {
    private final String dni;
    private final String rol;
}
