package com.owo.TP_prg3.Clases.Puesto.service;

import com.owo.TP_prg3.Clases.Puesto.dto.CreatePuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.UpdatePuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Clases.Puesto.modelo.PuestoRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio; // Para inyectar el repositorio de Entidad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class PuestoServicioImpl implements PuestoServicio {

    @Autowired
    private PuestoRepositorio puestoRepositorio;
    @Autowired
    private EntidadRepositorio entidadRepositorio; // Necesario para buscar la entidad dueña

    private PuestoDTO convertirA_DTO(Puesto puesto) {
        return new PuestoDTO(
                puesto.getPuestoId(),
                puesto.getNombre(),
                puesto.getDuenio() != null ? puesto.getDuenio().getEntidad_id() : null
        );
    }

    private Puesto convertirA_Puesto(CreatePuestoDTO puestoDTO) {
        Puesto puesto = new Puesto();
        puesto.setNombre(puestoDTO.getNombre());

        entidadRepositorio.findById(puestoDTO.getDuenioId())
                .ifPresentOrElse(
                        puesto::setDuenio,
                        () -> { throw new EntityNotFoundException("Entidad (dueño) con ID " + puestoDTO.getDuenioId() + " no encontrada."); }
                );

        return puesto;
    }

    @Override
    public List<PuestoDTO> getAllPuestos() {
        return puestoRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<PuestoDTO> getPuestoById(Long id) {
        return puestoRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    @Transactional
    public PuestoDTO createPuesto(CreatePuestoDTO createPuestoDTO) {
        Puesto puesto = convertirA_Puesto(createPuestoDTO);
        Puesto savedPuesto = puestoRepositorio.save(puesto);
        return convertirA_DTO(savedPuesto);
    }

    @Override
    @Transactional
    public Optional<PuestoDTO> updatePuesto(Long id, UpdatePuestoDTO updatePuestoDTO) {
        return puestoRepositorio.findById(id)
                .map(puesto -> {
                    if (updatePuestoDTO.getNombre() != null) {
                        puesto.setNombre(updatePuestoDTO.getNombre());
                    }
                    if (updatePuestoDTO.getDuenioId() != null) {
                        entidadRepositorio.findById(updatePuestoDTO.getDuenioId())
                                .ifPresentOrElse(
                                        puesto::setDuenio,
                                        () -> { throw new EntityNotFoundException("Entidad (dueño) con ID " + updatePuestoDTO.getDuenioId() + " no encontrada."); }
                                );
                    } else if (puesto.getDuenio() != null) { // Para desasociar el dueño del puesto
                        puesto.setDuenio(null);
                    }
                    Puesto updatedPuesto = puestoRepositorio.save(puesto);
                    return convertirA_DTO(updatedPuesto);
                });
    }

    @Override
    public boolean deletePuesto(Long id) {
        if (puestoRepositorio.existsById(id)) {
            puestoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
