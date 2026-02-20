package com.owo.TP_prg3.Clases.Persona.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialPersonaServicio {

    private final HistorialPersonaRepositorio repositorio;

    public HistorialPersonaServicio(HistorialPersonaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialPersona> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }


    @Transactional(readOnly = true)
    public List<HistorialPersona> obtenerPorDni(Integer dni) {
        return repositorio.findByDniOrderByFechaEventoDesc(dni);
    }
}

