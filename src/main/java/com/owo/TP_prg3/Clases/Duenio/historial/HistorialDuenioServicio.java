package com.owo.TP_prg3.Clases.Duenio.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialDuenioServicio {

    private final HistorialDuenioRepositorio repositorio;

    public HistorialDuenioServicio(HistorialDuenioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialDuenio> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
