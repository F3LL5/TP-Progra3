package com.owo.TP_prg3.Clases.Cliente.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/clientes")
public class HistorialClienteControlador {

    private final HistorialClienteServicio servicio;

    public HistorialClienteControlador(HistorialClienteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialCliente> global() {
        return servicio.obtenerTodo();
    }
}