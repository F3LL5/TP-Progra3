package com.owo.TP_prg3.Auditoria.DuenioHistorial.service;

import com.owo.TP_prg3.Auditoria.DuenioHistorial.dto.DuenioHistorialDTO;
import java.util.List;
import java.util.Optional;

public interface DuenioHistorialServicio {
    List<DuenioHistorialDTO> getAll();
    Optional<DuenioHistorialDTO> buscarPorId(Long id);
    String listado();
}
