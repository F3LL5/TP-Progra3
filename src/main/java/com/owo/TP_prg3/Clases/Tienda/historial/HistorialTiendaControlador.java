package com.owo.TP_prg3.Clases.Tienda.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/tiendas")
public class HistorialTiendaControlador {

    private final HistorialTiendaServicio servicio;

    public HistorialTiendaControlador(HistorialTiendaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialTienda> global() {
        return servicio.obtenerTodo();
    }
}
