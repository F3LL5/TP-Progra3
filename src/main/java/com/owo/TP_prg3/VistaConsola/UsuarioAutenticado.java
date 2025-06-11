package com.owo.TP_prg3.VistaConsola;

/**
 * Representa al usuario que ha iniciado sesión.
 */
public class UsuarioAutenticado {
    private final String dni;
    private final String rol;

    public UsuarioAutenticado(String dni, String rol) {
        this.dni = dni;
        this.rol = rol;
    }

    public String getDni() {
        return dni;
    }

    public String getRol() {
        return rol;
    }
}
