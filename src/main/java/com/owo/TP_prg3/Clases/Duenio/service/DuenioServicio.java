package com.owo.TP_prg3.Clases.Duenio.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.owo.TP_prg3.Clases.Duenio.dto.DuenioDTO;
import com.owo.TP_prg3.Clases.Duenio.dto.FormDuenioDTO;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Duenio.modelo.DuenioRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Usuario.modelo.RolUsuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;

@Service
public class DuenioServicio implements I_CRUD<Duenio, DuenioDTO, FormDuenioDTO> {

    @Autowired
    private DuenioRepositorio duenioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Conversion
    @Override
    public Duenio convertir_a_Obj(FormDuenioDTO fDTO) {
        Duenio duenio = new Duenio();
        duenio.setNombre(fDTO.getNombre());
        duenio.setEdad(fDTO.getEdad());
        duenio.setDni(fDTO.getDni());
        Usuario usuario = new Usuario(null, fDTO.getEmail(), passwordEncoder.encode(fDTO.getContraseña()), RolUsuario.ROLE_DUENIO);
        duenio.setUsuario(usuario);
        return duenio;
    }

    @Override
    public DuenioDTO convertir_a_DTO(Duenio duenio) {
        return new DuenioDTO(
            duenio.getPersonaId(), 
            duenio.getDni(),
            duenio.getNombre(),
            duenio.getEdad(),
            duenio.getDuenioId(),
            duenio.getUsuario().getEmail()
        );
    }

    @Override
    public Set<DuenioDTO> obtenerTodos() {
        return duenioRepositorio.findAll().stream() // JPA automáticamente hace el JOIN entre las tablas 'personas' y 'duenios'.
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<DuenioDTO> buscarPorID(Long id) {
        return duenioRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<DuenioDTO> buscarPorDNI(int dni) {
        return duenioRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<DuenioDTO> filtrar(String campo, Object valor) {
        Stream<Duenio> stream = duenioRepositorio.findAll().stream();

        Predicate<Duenio> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "edad" -> filtro = p -> p.getEdad().equals(valor);
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<DuenioDTO> ordenar(String campo, boolean ascendente) {
        Stream<Duenio> stream = duenioRepositorio.findAll().stream();

        Comparator<Duenio> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Duenio::getNombre);
            case "edad"-> comparador = Comparator.comparing(Duenio::getEdad);
            case "dni"-> comparador = Comparator.comparing(Duenio::getDni);
            default -> comparador = Comparator.comparing(Duenio::getDuenioId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    public boolean cargar(FormDuenioDTO createDTO) {
        if (this.buscarPorDNI(createDTO.getDni()).isPresent()) return false;
        duenioRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormDuenioDTO updateDTO) {
        Optional<Duenio> optional = duenioRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Duenio Duenio = optional.get();
        Duenio.setNombre(updateDTO.getNombre());
        Duenio.setEdad(updateDTO.getEdad());
        Duenio.setDni(updateDTO.getDni());

        duenioRepositorio.save(Duenio);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Optional<Duenio> optional = duenioRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else duenioRepositorio.delete(optional.get());
        return true;
    }

}
