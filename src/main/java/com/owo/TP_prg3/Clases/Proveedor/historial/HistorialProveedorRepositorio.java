package com.owo.TP_prg3.Clases.Proveedor.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialProveedorRepositorio extends JpaRepository<HistorialProveedor, Long> {
    List<HistorialProveedor> findAllByOrderByFechaEventoDesc();
}
