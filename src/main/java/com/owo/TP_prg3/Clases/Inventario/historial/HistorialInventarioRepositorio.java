package com.owo.TP_prg3.Clases.Inventario.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialInventarioRepositorio extends JpaRepository<HistorialInventario, Long> {
    List<HistorialInventario> findAllByOrderByFechaEventoDesc();
}
