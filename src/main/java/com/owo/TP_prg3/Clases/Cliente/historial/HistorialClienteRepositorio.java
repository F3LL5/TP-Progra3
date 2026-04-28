package com.owo.TP_prg3.Clases.Cliente.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialClienteRepositorio extends JpaRepository<HistorialCliente, Long> {
    List<HistorialCliente> findAllByOrderByFechaEventoDesc();
}