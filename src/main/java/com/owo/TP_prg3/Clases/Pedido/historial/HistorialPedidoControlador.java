package com.owo.TP_prg3.Clases.Pedido.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/pedidos")
public class HistorialPedidoControlador {

    private final HistorialPedidoServicio servicio;

    public HistorialPedidoControlador(HistorialPedidoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialPedido> global() {
        return servicio.obtenerTodo();
    }
}
