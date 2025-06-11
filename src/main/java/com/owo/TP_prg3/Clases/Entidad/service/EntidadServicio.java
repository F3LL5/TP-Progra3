package com.owo.TP_prg3.Clases.Entidad.service;

import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;

import java.util.List;
import java.util.Optional;

public interface EntidadServicio {
    List<EntidadDTO> getAllEntidades();
    Optional<EntidadDTO> getEntidadById(Long id);
    EntidadDTO createEntidad(CreateEntidadDTO createEntidadDTO);
    Optional<EntidadDTO> updateEntidad(Long id, UpdateEntidadDTO updateEntidadDTO);
    boolean deleteEntidad(Long id);
    Optional<Entidad> findByDni(int dni);
}