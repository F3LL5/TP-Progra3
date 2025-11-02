package com.owo.TP_prg3.Clases.Persona.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
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

    // POST

    // Metodo para crear cuenta y persona aleatoria

    // @Transactional
    // public PersonaDTO createEntidadYCuentaBancariaPuesto(Long puestoId,FormPersonaDTO createEntidadDTO) {
    //     if (createEntidadDTO.getRolEntidad() != RolEntidad.CLIENTE && createEntidadDTO.getRolEntidad() != RolEntidad.PROVEEDOR) {
    //         throw new IngresoInvalidoException("Solo se pueden crear entidades con rol CLIENTE o PROVEEDOR para un puesto.");
    //     }

    //     Persona entidad = convertirA_Entidad(createEntidadDTO);
    //     Persona entidadRegistrada = entidadRepositorio.save(entidad);

    //     //Creamos una cuenta bancaria automaticamente con un saldo random para agilizar el sistema
    //     if (entidadRegistrada.getEdad() >= 18) { // Solo si la entidad es mayor de edad
    //         CuentaBancaria cuentaBancaria = new CuentaBancaria();
    //         cuentaBancaria.setEntidad(entidadRegistrada);
    //         // Saldo random: 10,000 * (número aleatorio entre 1 y 10)
    //         double num = (int)(Math.random() * 10) + 1; // Número aleatorio entre 1 y 10
    //         cuentaBancaria.setSaldo(BigDecimal.valueOf(num * 10000.0));
    //         cuentaBancariaRepositorio.save(cuentaBancaria);
    //     }

    //     return convertirA_DTO(entidadRegistrada);
    // }

    @Override
    public boolean cargar(FormPersonaDTO cDTO) {
        personaRepositorio.save(convertir_a_Obj(cDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormPersonaDTO updateDTO) {
        Optional<Persona> optional = personaRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Persona persona = optional.get();
        persona.setNombre(updateDTO.getNombre());
        persona.setEdad(updateDTO.getEdad());
        persona.setDni(updateDTO.getDni());

        personaRepositorio.save(persona);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Optional<Persona> optional = personaRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else personaRepositorio.delete(optional.get());
        return true;
    }
}