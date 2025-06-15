package com.owo.TP_prg3.Auditoria.ItemCostoHistorial.service;

import com.owo.TP_prg3.Auditoria.ItemCostoHistorial.dto.ItemCostoHistorialDTO;

import java.util.List;
import java.util.Optional;

public interface ItemCostoHistorialServicio {
    List<ItemCostoHistorialDTO> getAll();
    Optional<ItemCostoHistorialDTO>buscarPorID(Long id);
    String listado();
}
