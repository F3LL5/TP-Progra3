package com.owo.TP_prg3.Clases.Inventario.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/inventarios")
public class HistorialInventarioControlador {

    private final HistorialInventarioServicio servicio;

    public HistorialInventarioControlador(HistorialInventarioServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialInventario> global() {
        return servicio.obtenerTodo();
    }
}
