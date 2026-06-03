package com.owo.TP_prg3.Clases.Producto.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialProductoServicio {

    private final HistorialProductoRepositorio repositorio;

    public HistorialProductoServicio(HistorialProductoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialProducto> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
