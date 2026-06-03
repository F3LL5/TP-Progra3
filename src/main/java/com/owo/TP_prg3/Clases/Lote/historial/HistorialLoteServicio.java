package com.owo.TP_prg3.Clases.Lote.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialLoteServicio {

    private final HistorialLoteRepositorio repositorio;

    public HistorialLoteServicio(HistorialLoteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialLote> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
