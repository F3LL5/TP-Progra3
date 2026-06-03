package com.owo.TP_prg3.Clases.Lote.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/lotes")
public class HistorialLoteControlador {

    private final HistorialLoteServicio servicio;

    public HistorialLoteControlador(HistorialLoteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialLote> global() {
        return servicio.obtenerTodo();
    }
}
