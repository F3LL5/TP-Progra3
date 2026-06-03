package com.owo.TP_prg3.Clases.DetallePedido.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/detalles-pedido")
public class HistorialDetallePedidoControlador {

    private final HistorialDetallePedidoServicio servicio;

    public HistorialDetallePedidoControlador(HistorialDetallePedidoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialDetallePedido> global() {
        return servicio.obtenerTodo();
    }
}
