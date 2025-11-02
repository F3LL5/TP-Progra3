package com.owo.TP_prg3.Clases.Proveedor.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Proveedor.dto.ProveedorDTO;
import com.owo.TP_prg3.Clases.Proveedor.modelo.Proveedor;
import com.owo.TP_prg3.Clases.Proveedor.modelo.ProveedorRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;

@Service
public class ProveedorServicio implements I_CRUD<Proveedor, ProveedorDTO, FormPersonaDTO>{

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    // Conversión
    @Override
    public Proveedor convertir_a_Obj(FormPersonaDTO fDTO) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(fDTO.getNombre());
        proveedor.setEdad(fDTO.getEdad());
        proveedor.setDni(fDTO.getDni());
        return proveedor;
    }
    
    @Override
    public ProveedorDTO convertir_a_DTO(Proveedor proveedor) {
        return new ProveedorDTO(
            proveedor.getPersonaId(), 
            proveedor.getDni(),
            proveedor.getNombre(),
            proveedor.getEdad(),
            proveedor.getProveedorId()
        );
    }

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    // GET
    @Override
    public Set<ProveedorDTO> obtenerTodos() {
        return proveedorRepositorio.findAll().stream() // JPA automáticamente hace el JOIN entre las tablas 'personas' y 'proveedores'.
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<ProveedorDTO> buscarPorID(Long id) {
        return proveedorRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<ProveedorDTO> buscarPorDNI(int dni) {
        return proveedorRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<ProveedorDTO> filtrar(String campo, Object valor) {

        Stream<Proveedor> stream = proveedorRepositorio.findAll().stream();

        Predicate<Proveedor> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "edad" -> filtro = p -> p.getEdad().equals(valor);
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<ProveedorDTO> ordenar(String campo, boolean ascendente) {
        Stream<Proveedor> stream = proveedorRepositorio.findAll().stream();

        Comparator<Proveedor> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Proveedor::getNombre);
            case "edad"-> comparador = Comparator.comparing(Proveedor::getEdad);
            case "dni"-> comparador = Comparator.comparing(Proveedor::getDni);
            default -> comparador = Comparator.comparing(Proveedor::getProveedorId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    public boolean cargar(FormPersonaDTO createDTO) {
        if (this.buscarPorDNI(createDTO.getDni()).isPresent()) return false;
        // JPA automáticamente realiza el INSERT en la tabla 'personas' y luego en 'proveedors'
        proveedorRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormPersonaDTO updateDTO) {
        Optional<Proveedor> optional = proveedorRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Proveedor proveedor = optional.get();
        proveedor.setNombre(updateDTO.getNombre());
        proveedor.setEdad(updateDTO.getEdad());
        proveedor.setDni(updateDTO.getDni());

        proveedorRepositorio.save(proveedor);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Optional<Proveedor> optional = proveedorRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else proveedorRepositorio.delete(optional.get());
        return true;
    }
}
