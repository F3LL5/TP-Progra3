package com.owo.TP_prg3.Clases.Persona.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/personas")
public class HistorialPersonaControlador {

    private final HistorialPersonaServicio servicio;

    public HistorialPersonaControlador(HistorialPersonaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialPersona> global() {
        return servicio.obtenerTodo();
    }
}

