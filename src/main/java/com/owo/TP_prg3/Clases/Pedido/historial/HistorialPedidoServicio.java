package com.owo.TP_prg3.Clases.Pedido.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialPedidoServicio {

    private final HistorialPedidoRepositorio repositorio;

    public HistorialPedidoServicio(HistorialPedidoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialPedido> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
