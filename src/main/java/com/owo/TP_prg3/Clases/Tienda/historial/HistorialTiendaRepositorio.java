package com.owo.TP_prg3.Clases.Tienda.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialTiendaRepositorio extends JpaRepository<HistorialTienda, Long> {
    List<HistorialTienda> findAllByOrderByFechaEventoDesc();
}
