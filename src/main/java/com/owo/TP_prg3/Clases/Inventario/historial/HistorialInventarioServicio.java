package com.owo.TP_prg3.Clases.Inventario.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialInventarioServicio {

    private final HistorialInventarioRepositorio repositorio;

    public HistorialInventarioServicio(HistorialInventarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialInventario> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
