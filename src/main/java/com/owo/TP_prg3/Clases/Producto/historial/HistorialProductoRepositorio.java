package com.owo.TP_prg3.Clases.Producto.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialProductoRepositorio extends JpaRepository<HistorialProducto, Long> {
    List<HistorialProducto> findAllByOrderByFechaEventoDesc();
}
