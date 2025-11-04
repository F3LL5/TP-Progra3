package com.owo.TP_prg3.Clases.Tienda.service;

import com.owo.TP_prg3.Excepciones.ConflictoDeDatosException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
import com.owo.TP_prg3.Clases.Tienda.dto.CreatePuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.UpdatePuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PuestoServicioImpl implements PuestoServicio {

    @Autowired
    private TiendaRepositorio puestoRepositorio;
    @Autowired
    private PersonaRepositorio entidadRepositorio; // Necesario para buscar la entidad dueña


    private PuestoDTO convertirA_DTO(Tienda puesto) {
        return new PuestoDTO(
                        puesto.getPuestoId(),
                        puesto.getNombre(),
                puesto.getComision(),
                puesto.getDuenio() != null ? puesto.getDuenio().getPersonaId() : null
                );
    }

    private Tienda convertirA_Puesto(CreatePuestoDTO puestoDTO) {
        Tienda puesto = new Tienda();
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
        return Optional.of(puestoRepositorio.findById(id)
                .map(this::convertirA_DTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto con ID " + id + " no encontrado.")));
    }

    @Override
    @Transactional
    public PuestoDTO createPuesto(CreatePuestoDTO createPuestoDTO) {
        Persona entidadAsociada = entidadRepositorio.findById(createPuestoDTO.getDuenioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe entidad con ID proporcionado."));

        int edad = entidadAsociada.getEdad();
        if (edad < 18) {
            throw new IngresoInvalidoException("La entidades menores de edad NO pueden tener puestos");
        }

        if (createPuestoDTO.getComision() != null) {
            if (createPuestoDTO.getComision().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IngresoInvalidoException("La comisión debe ser mayor a cero.");
            }
        }

        Optional<PuestoDTO> existe = getAllPuestos().stream().filter(puesto -> puesto.getDuenioId() == createPuestoDTO.getDuenioId()).findFirst();
        if (existe.isPresent()) {
            throw new ConflictoDeDatosException("Entidad de dicho ID ya posee un puesto");
        }
        Tienda puesto = convertirA_Puesto(createPuestoDTO);
        Tienda savedPuesto = puestoRepositorio.save(puesto);
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
                        if (updatePuestoDTO.getComision().compareTo(BigDecimal.ZERO) < 0) {
                            throw new IngresoInvalidoException("La comisión no puede ser un número negativo.");
                        }
                        puesto.setComision(updatePuestoDTO.getComision());
                    }

                    if (updatePuestoDTO.getDuenioId() != null) {
                        if (updatePuestoDTO.getDuenioId() == 0) {
                            puesto.setDuenio(null);
                        } else {
                            Persona duenio = entidadRepositorio.findById(updatePuestoDTO.getDuenioId())
                                    .orElseThrow(() -> new RecursoNoEncontradoException("Entidad (dueño) con ID " + updatePuestoDTO.getDuenioId() + " no encontrada."));
                            puesto.setDuenio(duenio);
                        }
                    }

                    Tienda updatedPuesto = puestoRepositorio.save(puesto);
                    return convertirA_DTO(updatedPuesto);
                }).or(() -> {
                    throw new RecursoNoEncontradoException("Puesto con ID " + id + " no encontrado.");
                });
    }

    @Transactional
    @Override
    public Optional<PuestoDTO> updateMiPuesto(Long idPuesto, UpdatePuestoDTO updatePuestoDTO, Long idDuenio) {
        return puestoRepositorio.findById(idPuesto)
                .map(puesto -> {
                    Optional<Persona> optionalEntidad = entidadRepositorio.findById(idDuenio);
                    Persona duenioActual = optionalEntidad.orElseThrow(() -> new RecursoNoEncontradoException("Dueño con ID " + idDuenio + " no encontrado."));

                    // Si se ingresó, actualiza el nombre
                    if (updatePuestoDTO.getNombre() != null) {
                        puesto.setNombre(updatePuestoDTO.getNombre());
                    }

                    // Si se ingresó, actualiza el duenio o lo desasocia
                    if (updatePuestoDTO.getDuenioId() != null) {
                        if (Long.valueOf(0).equals(updatePuestoDTO.getDuenioId())) {
                            puesto.setDuenio(null);
                        } else {
                            Persona nuevoDuenio = entidadRepositorio.findById(updatePuestoDTO.getDuenioId())
                                    .orElseThrow(() -> new RecursoNoEncontradoException("Entidad (dueño) con ID " + updatePuestoDTO.getDuenioId() + " no encontrada."));

                            Optional<PuestoDTO> optionalPuesto = getPuestoByDni(nuevoDuenio.getDni());
                            if (optionalPuesto.isPresent()) throw new RecursoNoEncontradoException("Dueño con ID " + nuevoDuenio.getPersonaId() + " ya posee un puesto asignado.");

                            puesto.setDuenio(nuevoDuenio);
                        }
                    }

                    // Verifica que la comisión sea superior a cero (al final no era tan relevante porque esto lo verifica el Escaner de porcetaje pero equis)
                    if (updatePuestoDTO.getComision()!= null && updatePuestoDTO.getComision().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("La comisión debe ser mayor que 0.");
                    }
                    puesto.setComision(updatePuestoDTO.getComision());

                    // Guarda los cambios y devuelve el registro modificado
                    Tienda actualizado = puestoRepositorio.save(puesto);
                    return convertirA_DTO(actualizado);
                }).or(() -> {
                    throw new RecursoNoEncontradoException("Puesto con ID " + idPuesto + " no encontrado.");
                });
    }

    @Override
    public boolean deletePuesto(Long id) {
        if (!puestoRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Puesto con ID " + id + " no encontrado.");
        }
        puestoRepositorio.deleteById(id);
        return true;
    }

    @Override
    public List<PuestoDTO> filtrarYOrdenarPorNombre(String sortBy, String sortDir) {
        List<PuestoDTO> allPuestos = getAllPuestos();
        Stream<PuestoDTO> puestoDTOStream = allPuestos.stream();

        if (sortDir != null && !sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IngresoInvalidoException("La dirección de ordenamiento debe ser 'asc' o 'desc'.");
        }

        Comparator<PuestoDTO> comparator = null;
        if(sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "nombre" -> comparator = Comparator.comparing(PuestoDTO::getNombre);
                case "comision" -> comparator = Comparator.comparing(PuestoDTO::getComision);
                default -> throw new RuntimeException("Dicho criterio NO existe.");
            }
        }
        if(comparator != null) {
            if ("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();
        } else {
            comparator = Comparator.comparing(PuestoDTO::getPuestoId);
        }
        puestoDTOStream = puestoDTOStream.sorted(comparator);

        return puestoDTOStream.sorted(comparator).toList();
    }

    @Override
    public Optional<PuestoDTO> getPuestoByDni(int dni) {
        List<Persona> entidades = entidadRepositorio.findAll();

        Long duenioId = entidades.stream()
                .filter(entidad -> entidad.getDni().equals(dni))
                .map(Persona::getPersonaId)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró ninguna entidad con DNI " + dni + "."));

        return getAllPuestos().stream()
                .filter(puesto -> puesto.getDuenioId() != null && puesto.getDuenioId().equals(duenioId))
                .findFirst()
                .or(() -> {
                    throw new RecursoNoEncontradoException("No se encontró ningún puesto con dueño de DNI " + dni + ".");
                });
    }
}
