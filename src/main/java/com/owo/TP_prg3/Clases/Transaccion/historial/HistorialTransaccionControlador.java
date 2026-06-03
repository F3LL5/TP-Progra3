package com.owo.TP_prg3.Clases.Transaccion.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/transacciones")
public class HistorialTransaccionControlador {

    private final HistorialTransaccionServicio servicio;

    public HistorialTransaccionControlador(HistorialTransaccionServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialTransaccion> global() {
        return servicio.obtenerTodo();
    }
}
