package com.owo.TP_prg3.Clases.Empleado.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialEmpleadoRepositorio extends JpaRepository<HistorialEmpleado, Long> {
    List<HistorialEmpleado> findAllByOrderByFechaEventoDesc();
}
