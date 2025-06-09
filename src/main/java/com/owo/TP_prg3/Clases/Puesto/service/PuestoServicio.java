package com.owo.TP_prg3.Clases.Puesto.service;

import com.owo.TP_prg3.Clases.Puesto.dto.CreatePuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.UpdatePuestoDTO;

import java.util.List;
import java.util.Optional;

public interface PuestoServicio {
    List<PuestoDTO> getAllPuestos();
    Optional<PuestoDTO> getPuestoById(Long id);
    PuestoDTO createPuesto(CreatePuestoDTO createPuestoDTO);
    Optional<PuestoDTO> updatePuesto(Long id, UpdatePuestoDTO updatePuestoDTO);
    boolean deletePuesto(Long id);
}
