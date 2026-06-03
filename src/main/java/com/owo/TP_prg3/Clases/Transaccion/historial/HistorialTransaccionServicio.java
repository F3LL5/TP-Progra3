package com.owo.TP_prg3.Clases.Transaccion.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialTransaccionServicio {

    private final HistorialTransaccionRepositorio repositorio;

    public HistorialTransaccionServicio(HistorialTransaccionRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialTransaccion> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
