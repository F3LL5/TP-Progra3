package com.owo.TP_prg3.Clases.Producto.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/productos")
public class HistorialProductoControlador {

    private final HistorialProductoServicio servicio;

    public HistorialProductoControlador(HistorialProductoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialProducto> global() {
        return servicio.obtenerTodo();
    }
}
