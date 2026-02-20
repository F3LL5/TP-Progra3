package com.owo.TP_prg3.Clases.Persona.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialPersonaRepositorio extends JpaRepository<HistorialPersona, Long> {
    List<HistorialPersona> findByDniOrderByFechaEventoDesc(Integer dni);

    List<HistorialPersona> findAllByOrderByFechaEventoDesc();
}
