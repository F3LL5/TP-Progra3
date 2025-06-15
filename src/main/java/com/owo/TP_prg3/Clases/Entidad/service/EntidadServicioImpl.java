package com.owo.TP_prg3.Clases.Entidad.service;

import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import org.hibernate.cache.spi.support.AbstractRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EntidadServicioImpl implements EntidadServicio {

    @Autowired
    private EntidadRepositorio entidadRepositorio;

    // Conversión
    private EntidadDTO convertirA_DTO(Entidad entidad) {
        return new EntidadDTO(
                entidad.getEntidad_id(),
                entidad.getNombre(),
                entidad.getRolEntidad(),
                entidad.getTipoEntidad(),
                entidad.getEdad(),
                entidad.getDni()
        );
    }

    private Entidad convertirA_Entidad(CreateEntidadDTO entidadDTO) {
        Entidad entidad = new Entidad();
        entidad.setNombre(entidadDTO.getNombre());
        entidad.setRolEntidad(entidadDTO.getRolEntidad());
        entidad.setTipoEntidad(entidadDTO.getTipoEntidad());
        entidad.setEdad(entidadDTO.getEdad());
        entidad.setDni(entidadDTO.getDni());
        return entidad;
    }

    // Métodos
    @Override
    public List<EntidadDTO> getAllEntidades() {
        return entidadRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<EntidadDTO> getEntidadById(Long id) {
        return entidadRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    public EntidadDTO createEntidad(CreateEntidadDTO createEntidadDTO) {
        Entidad entidad = convertirA_Entidad(createEntidadDTO);
        Entidad entidadRegistrada = entidadRepositorio.save(entidad);
        return convertirA_DTO(entidadRegistrada);
    }

    @Override
    public Optional<EntidadDTO> updateEntidad(Long id, UpdateEntidadDTO updateEntidadDTO) {
        return entidadRepositorio.findById(id)
                .map(entidad -> {
                    if (updateEntidadDTO.getNombre() != null) {
                        entidad.setNombre(updateEntidadDTO.getNombre());
                    }
                    if (updateEntidadDTO.getRolEntidad() != null) {
                        entidad.setRolEntidad(updateEntidadDTO.getRolEntidad());
                    }
                    if (updateEntidadDTO.getTipoEntidad() != null) {
                        entidad.setTipoEntidad(updateEntidadDTO.getTipoEntidad());
                    }
                    if (updateEntidadDTO.getEdad() != null) {
                        entidad.setEdad(updateEntidadDTO.getEdad());
                    }
                    if (updateEntidadDTO.getDni() != null) {
                        entidad.setDni(updateEntidadDTO.getDni());
                    }
                    Entidad entidadModificada = entidadRepositorio.save(entidad);
                    return convertirA_DTO(entidadModificada);
                });
    }

    @Override
    public boolean deleteEntidad(Long id) {
        if (entidadRepositorio.existsById(id)) {
            entidadRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Entidad> findByDni(int dni) {
        return entidadRepositorio.findAll()
                .stream()
                .filter(entidad -> entidad.getDni() == dni)
                .findFirst();
    }

    @Override
    public List<EntidadDTO> filtrarYOrdenar(String rol_entidad, String sortBy, String sortDir) {
        List<EntidadDTO> allEntitys = getAllEntidades();
        Stream<EntidadDTO> entidadDTOStream = allEntitys.stream();

        if (rol_entidad != null && !rol_entidad.isEmpty()) {
            entidadDTOStream = entidadDTOStream.filter(entidad -> entidad.getRolEntidad().name().equalsIgnoreCase(rol_entidad));
        }

        if(sortBy != null) {
            Comparator<EntidadDTO> comparator = null;
            switch (sortBy.toLowerCase()) {
                case "nombre" -> comparator = Comparator.comparing(EntidadDTO::getNombre);
                case "edad" -> comparator = Comparator.comparing(EntidadDTO::getEdad);
                case "dni" -> comparator = Comparator.comparing(EntidadDTO::getDni);
                default -> throw new RuntimeException("Dicho criterio NO existe.");
            }
            if(comparator != null) if ("desc".equalsIgnoreCase(sortDir)) comparator = comparator.reversed();

            entidadDTOStream = entidadDTOStream.sorted(comparator);
        }

        return entidadDTOStream.toList();
    }
}