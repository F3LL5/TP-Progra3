package com.owo.TP_prg3.Clases.Proveedor.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/proveedores")
public class HistorialProveedorControlador {

    private final HistorialProveedorServicio servicio;

    public HistorialProveedorControlador(HistorialProveedorServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialProveedor> global() {
        return servicio.obtenerTodo();
    }
}
