package com.owo.TP_prg3.Clases.Usuario.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialUsuarioServicio {

    private final HistorialUsuarioRepositorio repo;

    public HistorialUsuarioServicio(HistorialUsuarioRepositorio repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<HistorialUsuario> obtenerTodo() {
        return repo.findAllByOrderByFechaEventoDesc();
    }

    @Transactional(readOnly = true)
    public List<HistorialUsuario> obtenerPorUsuario(Long usuarioId) {
        return repo.findByUsuarioIdOrderByFechaEventoDesc(usuarioId);
    }
}