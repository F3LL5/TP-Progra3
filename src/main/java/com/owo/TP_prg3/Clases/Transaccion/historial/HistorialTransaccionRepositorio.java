package com.owo.TP_prg3.Clases.Transaccion.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialTransaccionRepositorio extends JpaRepository<HistorialTransaccion, Long> {
    List<HistorialTransaccion> findAllByOrderByFechaEventoDesc();
}
