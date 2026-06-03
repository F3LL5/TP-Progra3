package com.owo.TP_prg3.Clases.Lote.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialLoteRepositorio extends JpaRepository<HistorialLote, Long> {
    List<HistorialLote> findAllByOrderByFechaEventoDesc();
}
