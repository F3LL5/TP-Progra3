package com.owo.TP_prg3.Clases.DetallePedido.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialDetallePedidoServicio {

    private final HistorialDetallePedidoRepositorio repositorio;

    public HistorialDetallePedidoServicio(HistorialDetallePedidoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialDetallePedido> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
