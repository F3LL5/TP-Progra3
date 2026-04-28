package com.owo.TP_prg3.Clases.Cliente.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialClienteServicio {

    private final HistorialClienteRepositorio repositorio;

    public HistorialClienteServicio(HistorialClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialCliente> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}