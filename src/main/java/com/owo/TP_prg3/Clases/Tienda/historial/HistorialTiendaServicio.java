package com.owo.TP_prg3.Clases.Tienda.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialTiendaServicio {

    private final HistorialTiendaRepositorio repositorio;

    public HistorialTiendaServicio(HistorialTiendaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialTienda> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
