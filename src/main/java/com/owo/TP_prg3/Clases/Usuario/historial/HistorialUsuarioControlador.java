package com.owo.TP_prg3.Clases.Usuario.historial;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historial/usuarios")
public class HistorialUsuarioControlador {


    private final HistorialUsuarioServicio servicio;

    public HistorialUsuarioControlador(HistorialUsuarioServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialUsuario> global() {
        return servicio.obtenerTodo();
    }

    @GetMapping("/{usuarioId}")
    public List<HistorialUsuario> porUsuario(@PathVariable Long usuarioId) {
        return servicio.obtenerPorUsuario(usuarioId);
    }
}