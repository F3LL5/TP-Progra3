package com.owo.TP_prg3.Clases.Duenio.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialDuenioRepositorio extends JpaRepository<HistorialDuenio, Long> {
    List<HistorialDuenio> findAllByOrderByFechaEventoDesc();
}
