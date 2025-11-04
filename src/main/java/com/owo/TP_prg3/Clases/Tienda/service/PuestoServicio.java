package com.owo.TP_prg3.Clases.Tienda.service;

import org.springframework.transaction.annotation.Transactional;

import com.owo.TP_prg3.Clases.Tienda.dto.CreatePuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.UpdatePuestoDTO;

import java.util.List;
import java.util.Optional;

public interface PuestoServicio {
    List<PuestoDTO> getAllPuestos();
    Optional<PuestoDTO> getPuestoById(Long id);
    PuestoDTO createPuesto(CreatePuestoDTO createPuestoDTO);
    Optional<PuestoDTO> updatePuesto(Long id, UpdatePuestoDTO updatePuestoDTO);

    @Transactional
    Optional<PuestoDTO> updateMiPuesto(Long idPuesto, UpdatePuestoDTO updatePuestoDTO, Long idDuenio);

    boolean deletePuesto(Long id);
    List<PuestoDTO> filtrarYOrdenarPorNombre(String sortBy, String sortDir);
    Optional<PuestoDTO> getPuestoByDni(int dni);
}
