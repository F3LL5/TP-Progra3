package com.owo.TP_prg3.Clases.Duenio.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/duenios")
public class HistorialDuenioControlador {

    private final HistorialDuenioServicio servicio;

    public HistorialDuenioControlador(HistorialDuenioServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialDuenio> global() {
        return servicio.obtenerTodo();
    }
}
