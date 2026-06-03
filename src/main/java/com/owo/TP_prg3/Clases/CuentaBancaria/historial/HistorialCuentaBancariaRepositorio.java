package com.owo.TP_prg3.Clases.CuentaBancaria.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialCuentaBancariaRepositorio extends JpaRepository<HistorialCuentaBancaria, Long> {
    List<HistorialCuentaBancaria> findAllByOrderByFechaEventoDesc();
}
