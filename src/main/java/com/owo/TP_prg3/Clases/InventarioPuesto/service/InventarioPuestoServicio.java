package com.owo.TP_prg3.Clases.InventarioPuesto.service;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.ItemStockDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface InventarioPuestoServicio {
    List<InventarioPuestoDTO> getAllInventarioPuestos();
    Optional<InventarioPuestoDTO> getInventarioPuestoById(Long id);
    List<InventarioPuestoDTO> obtenerInventariosDeUnPuesto(Long puestoId);
    Optional<List<ItemStockDTO>> obtenerItemsEnStockBajo(Long puestoId);
    List<InventarioPuestoDTO> filtrarYordenar(
            @RequestParam(required = false) Long puestoId, @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir);
    InventarioPuestoDTO createInventarioPuesto(CreateInventarioPuestoDTO createInventarioPuestoDTO);
    Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, UpdateInventarioPuestoDTO updateInventarioPuestoDTO);
    boolean deleteInventarioPuesto(Long id);

}
