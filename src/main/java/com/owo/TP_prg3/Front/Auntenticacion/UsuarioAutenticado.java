package com.owo.TP_prg3.Front.Auntenticacion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UsuarioAutenticado {
    private final String dni;
    private final String rol;
}
