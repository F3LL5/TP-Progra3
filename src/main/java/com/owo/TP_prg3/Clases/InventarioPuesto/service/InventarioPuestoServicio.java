package com.owo.TP_prg3.Clases.InventarioPuesto.service;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;

import java.util.List;
import java.util.Optional;

public interface InventarioPuestoServicio {
    List<InventarioPuestoDTO> getAllInventarioPuestos();
    Optional<InventarioPuestoDTO> getInventarioPuestoById(Long id);
    InventarioPuestoDTO createInventarioPuesto(CreateInventarioPuestoDTO createInventarioPuestoDTO);
    Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, UpdateInventarioPuestoDTO updateInventarioPuestoDTO);
    boolean deleteInventarioPuesto(Long id);
}
