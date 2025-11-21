package com.owo.TP_prg3.Clases.Persona.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PersonaServicio implements I_CRUD<Persona, PersonaDTO, FormPersonaDTO> {

    @Autowired
    private PersonaRepositorio personaRepositorio;

    // Conversión
    @Override
    public Persona convertir_a_Obj(FormPersonaDTO fDTO) {
        validarDatosPersona(fDTO);
        return new Persona(
            null,
            fDTO.getNombre(),
            fDTO.getEdad(),
            fDTO.getDni()
        );
    }

    @Override
    public PersonaDTO convertir_a_DTO(Persona persona) {
        return new PersonaDTO(
                persona.getPersonaId(),
                persona.getDni(),
                persona.getNombre(),
                persona.getEdad()
        );
    }

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    // GET
    @Override
    public Set<PersonaDTO> obtenerTodos() {
        return personaRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<PersonaDTO> buscarPorID(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return personaRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<PersonaDTO> buscarPorDNI(int dni) {
        return personaRepositorio.findByDni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<PersonaDTO> filtrar(String campo, Object valor) {

        Stream<Persona> stream = personaRepositorio.findAll().stream();

        Predicate<Persona> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "edad" -> filtro = p -> p.getEdad().equals(valor);
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<PersonaDTO> ordenar(String campo, boolean ascendente) {
        Stream<Persona> stream = personaRepositorio.findAll().stream();

        Comparator<Persona> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Persona::getNombre);
            case "edad"-> comparador = Comparator.comparing(Persona::getEdad);
            case "dni"-> comparador = Comparator.comparing(Persona::getDni);
            default -> comparador = Comparator.comparing(Persona::getPersonaId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public boolean cargar(FormPersonaDTO cDTO) {
        validarDatosPersona(cDTO);
        validarPersonaNoExiste(cDTO.getDni());
        personaRepositorio.save(convertir_a_Obj(cDTO));
        return true;
    }

    // PUT
    @Override
    @Transactional
    public boolean actualizar(Long id, FormPersonaDTO updateDTO) {
        validarDatosPersona(updateDTO);
        Persona persona = obtenerPersonaPorId(id);
        
        validarPersonaNoExisteOtro(updateDTO.getDni(), id);

        persona.setNombre(updateDTO.getNombre().trim());
        persona.setEdad(updateDTO.getEdad());
        persona.setDni(updateDTO.getDni());

        personaRepositorio.save(persona);
        return true;
    }

    // DELETE 
    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Persona persona = obtenerPersonaPorId(id);
        personaRepositorio.delete(persona);
        return true;
    }

    // VALIDACIONES
    public void validarDatosPersona(FormPersonaDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de Persona no pueden ser nulos");
        
        ValidacionGeneral.validarStringNoVacio(dto.getNombre(), "nombre");
        ValidacionGeneral.sinNumeros(dto.getNombre(), "nombre");
        ValidacionGeneral.validarDni(dto.getDni());
        
        if (dto.getEdad() == null) throw new CampoRequeridoException("edad");
        ValidacionGeneral.enRango(dto.getEdad(), 18, 120, "edad");
    }

    public void validarPersonaNoExiste(int dni) {
        Optional<PersonaDTO> existente = this.buscarPorDNI(dni);
        if (existente.isPresent()) {
            throw new EntidadDuplicadaException("Persona", "DNI", dni);
        }
    }
    
    public void validarPersonaNoExisteOtro(int dni, Long id) {
        Optional<Persona> existente = personaRepositorio.findByDni(dni);
        if (existente.isPresent() && !existente.get().getPersonaId().equals(id)) {
            throw new EntidadDuplicadaException("Persona", "DNI", dni);
        }
    }
    
    public Persona obtenerPersonaPorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return personaRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Persona", id));
    }
}