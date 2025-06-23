package com.owo.TP_prg3.Clases.Entidad.service;

import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface EntidadServicio {
    List<EntidadDTO> getAllEntidades();
    Optional<EntidadDTO> getEntidadById(Long id);
    List<EntidadDTO> getEntidadesByPuestoId(Long puestoId);
    List<EntidadDTO> getClientesConPedidosByPuestoId(Long puestoId);
    Optional<EntidadDTO> getEntidadByIdAndPuestoId(Long id, Long puestoId);
    EntidadDTO createEntidad(CreateEntidadDTO createEntidadDTO);
    Optional<EntidadDTO> updateEntidad(Long id, UpdateEntidadDTO updateEntidadDTO);
    boolean deleteEntidad(Long id);
    Optional<EntidadDTO> findByDni(int dni);
    List<EntidadDTO> filtrarYOrdenar(Long puestoId, String rol_entidad, String sortBy, String sortDir);

    @Transactional
    EntidadDTO createEntidadForPuesto(Long puestoId, CreateEntidadDTO createEntidadDTO);

    @Transactional
    EntidadDTO createEntidadYCuentaBancaria(CreateEntidadDTO createEntidadDTO);

    @Transactional
    Optional<EntidadDTO> updateEntidadForPuesto(Long id, Long puestoId, UpdateEntidadDTO updateEntidadDTO);

    @Transactional
    boolean deleteEntidadFromPuesto(Long id, Long puestoId);
}