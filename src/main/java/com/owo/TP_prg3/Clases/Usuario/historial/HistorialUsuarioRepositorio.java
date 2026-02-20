package com.owo.TP_prg3.Clases.Usuario.historial;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialUsuarioRepositorio extends JpaRepository<HistorialUsuario, Long> {
    List<HistorialUsuario> findAllByOrderByFechaEventoDesc();
    List<HistorialUsuario> findByUsuarioIdOrderByFechaEventoDesc(Long usuarioId);
}
