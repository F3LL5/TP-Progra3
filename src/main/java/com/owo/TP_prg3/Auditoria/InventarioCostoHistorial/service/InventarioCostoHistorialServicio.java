package com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.service;

import com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.dto.InventarioCostoHistorialDTO;

import java.util.List;
import java.util.Optional;

public interface InventarioCostoHistorialServicio {
    List<InventarioCostoHistorialDTO> getAll();
    Optional<InventarioCostoHistorialDTO>buscarPorID(Long id);
    String listado();
}
