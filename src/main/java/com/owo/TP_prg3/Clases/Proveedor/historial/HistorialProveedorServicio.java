package com.owo.TP_prg3.Clases.Proveedor.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialProveedorServicio {

    private final HistorialProveedorRepositorio repositorio;

    public HistorialProveedorServicio(HistorialProveedorRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialProveedor> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
