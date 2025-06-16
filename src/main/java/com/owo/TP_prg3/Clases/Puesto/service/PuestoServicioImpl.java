package com.owo.TP_prg3.Clases.Puesto.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
                puesto.getComision(),
                puesto.getDuenio() != null ? puesto.getDuenio().getEntidad_id() : null
                );
    }

    private Puesto convertirA_Puesto(CreatePuestoDTO puestoDTO) {
        Puesto puesto = new Puesto();
        puesto.setNombre(puestoDTO.getNombre());
        puesto.setComision(puestoDTO.getComision());

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
        Entidad entidadAsociada = entidadRepositorio.findById(createPuestoDTO.getDuenioId())
                .orElseThrow(() -> new RuntimeException("No existe entidad con ID proporcionado."));

        int edad = entidadAsociada.getEdad();
        if (edad < 18) {
            throw new RuntimeException("La entidades menores de edad NO pueden tener puestos");
        }

        Optional<PuestoDTO> existe = getAllPuestos().stream().filter(puesto -> puesto.getDuenioId() == createPuestoDTO.getDuenioId()).findFirst();
        if (existe.isPresent()) {
            throw new RuntimeException("Entidad de dicho ID ya posee un puesto");
        }
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
                    if (updatePuestoDTO.getComision() != null) {
                        puesto.setComision(updatePuestoDTO.getComision());
                    }
                    if (updatePuestoDTO.getDuenioId() != null) {
                        if (updatePuestoDTO.getDuenioId() == 0){
                            puesto.setDuenio(null);
                        } else {
                            entidadRepositorio.findById(updatePuestoDTO.getDuenioId())
                                    .ifPresentOrElse(
                                            puesto::setDuenio,
                                            () -> {
                                                throw new EntityNotFoundException("Entidad (dueño) con ID " + updatePuestoDTO.getDuenioId() + " no encontrada.");
                                            }
                                    );
                        }
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

    @Override
    public List<PuestoDTO> filtrarYOrdenarPorNombre(String nombre, String sortDir) {
        List<PuestoDTO> allPuestos = getAllPuestos();
        Stream<PuestoDTO> puestoDTOStream = allPuestos.stream();

        if(nombre != null && !nombre.isEmpty()) {
            puestoDTOStream = puestoDTOStream.filter(puesto -> puesto.getNombre().equalsIgnoreCase(nombre));
        }

        Comparator<PuestoDTO> comparator = Comparator.comparing(PuestoDTO::getNombre);
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return puestoDTOStream.sorted(comparator).toList();
    }

    public Optional<PuestoDTO> getPuestoByDni(Long dni) {

        List<PuestoDTO> puestos = getAllPuestos();
        List<Entidad> entidades = entidadRepositorio.findAll();

        // Buscamos el ID del dueño que tenga ese DNI
        Optional<Long> duenioId = entidades.stream()
                .filter(entidad -> entidad.getDni().equals(dni))
                .map(Entidad::getEntidad_id)
                .findFirst();

        if (duenioId.isPresent()) {
            return puestos.stream()
                    .filter(puesto -> puesto.getDuenioId().equals(duenioId.get()))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }
}
