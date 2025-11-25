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
import com.owo.TP_prg3.Clases.Enum.RolUsuario;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
import com.owo.TP_prg3.Clases.Persona.service.PersonaServicio;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ReglaNegocioException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;

import jakarta.transaction.Transactional;

@Service
public class DuenioServicio implements I_CRUD<Duenio, DuenioDTO, FormDuenioDTO> {

    private static final String ENTIDAD = "Dueño";

    @Autowired
    private DuenioRepositorio duenioRepositorio;

    @Autowired
    private PersonaServicio personaServicio;
    @Autowired
    private PersonaRepositorio personaRepositorio;

    @Autowired
    private TiendaRepositorio tiendaRepositorio;


    @Autowired
    private PasswordEncoder passwordEncoder;

    // Conversion
    @Override
    public Duenio convertir_a_Obj(FormDuenioDTO fDTO) {
        validarDatosDuenio(fDTO);
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
        ValidacionGeneral.validarIdValido(id);
        return duenioRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<DuenioDTO> buscarPorDNI(int dni) {
        if (dni <= 0) {
             throw new IngresoInvalidoException("DNI", "debe ser un número positivo.");
        }
        return duenioRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    public Optional<DuenioDTO> buscarPorEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
        throw new CampoRequeridoException("email");
    }
    ValidacionGeneral.validarEmailFormat(email, "email");

    return duenioRepositorio.findByUsuario_Email(email.trim())
            .map(this::convertir_a_DTO);
}

    @Override
    public Set<DuenioDTO> filtrar(String campo, Object valor) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de filtrado");
        }
        if (valor == null) {
            throw new CampoRequeridoException("valor de filtrado");
        }

        Stream<Duenio> stream = duenioRepositorio.findAll().stream();
        Predicate<Duenio> filtro;
        String campoTrim = campo.toLowerCase().trim();

        switch (campoTrim) {
            case "nombre" -> filtro = p -> p.getNombre().equalsIgnoreCase(valor.toString().trim()); 
            case "edad" -> {
                 try {
                     int edad = Integer.parseInt(valor.toString());
                     filtro = p -> p.getEdad().equals(edad);
                 } catch (NumberFormatException e) {
                     throw new IngresoInvalidoException("valor", "debe ser un número entero para el campo 'edad'");
                 }
            }
            case "dni" -> { 
                try {
                    int dni = Integer.parseInt(valor.toString());
                    filtro = p -> p.getDni() == dni;
                } catch (NumberFormatException e) {
                    throw new IngresoInvalidoException("valor", "debe ser un número entero para el campo 'dni'");
                }
            }
            case "email" -> filtro = p -> p.getUsuario().getEmail().equalsIgnoreCase(valor.toString().trim()); 
            default -> throw new IngresoInvalidoException( 
                "campo de filtrado",
                "debe ser 'nombre', 'edad', 'dni' o 'email'"
            );
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<DuenioDTO> ordenar(String campo, boolean ascendente) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de ordenamiento");
        }
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
    @Transactional
    public boolean cargar(FormDuenioDTO createDTO) {
        validarDatosDuenio(createDTO);
        validarDuenioNoExiste(createDTO.getDni(), createDTO.getEmail());
        duenioRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
   @Transactional
    public boolean actualizar(Long id, FormDuenioDTO updateDTO) {
        validarDatosDuenio(updateDTO);
        Duenio duenio = obtenerDuenioPorId(id);
        
        duenio.getPersona().setNombre(updateDTO.getNombre().trim());
        duenio.getPersona().setEdad(updateDTO.getEdad());
        duenio.getPersona().setDni(updateDTO.getDni());
        
        duenio.getUsuario().setEmail(updateDTO.getEmail().trim());
        if (updateDTO.getContraseña() != null && !updateDTO.getContraseña().trim().isEmpty()) {
            duenio.getUsuario().setContraseña(passwordEncoder.encode(updateDTO.getContraseña()));
        }
        
        duenioRepositorio.save(duenio);
        return true;
    }

    // DELETE 
    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Duenio duenio = obtenerDuenioPorId(id);
        
        boolean tieneTienda = tiendaRepositorio.existsByDuenio_DuenioId(duenio.getDuenioId());
        if (tieneTienda) throw new ReglaNegocioException("No se puede ELIMINAR el dueño porque posee una tienda activa. Elimine la tienda primero.");

        duenioRepositorio.delete(duenio);
        return true;
    }

    // VALIDACIONES PRIVADAS ===========================================================================================

    private void validarDatosDuenio(FormDuenioDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");

        // Validaciones de Persona
        personaServicio.validarDatosPersona(new FormPersonaDTO(dto.getNombre(), dto.getEdad(), dto.getDni()));
        
        // Validaciones de Usuario
        if (dto.getEmail() == null) throw new CampoRequeridoException("email");
        if (dto.getContraseña() == null) throw new CampoRequeridoException("contrasenia");
        
        ValidacionGeneral.validarEmailFormat(dto.getEmail(), "email");
        ValidacionGeneral.validarContrasenia(dto.getContraseña(), "contrasenia");
    }

    private void validarDuenioNoExiste(int dni, String email) { 
        Optional<DuenioDTO> existenteDni = this.buscarPorDNI(dni);
        Optional<Persona> existeDniPersona = personaRepositorio.findByDni(dni);
        if (existenteDni.isPresent() || existeDniPersona.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "DNI", dni);
        }
        
        Optional<Duenio> existenteEmail = duenioRepositorio.findByUsuario_Email(email.trim());
        if (existenteEmail.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }

    private Duenio obtenerDuenioPorId(Long id) { 
        ValidacionGeneral.validarIdValido(id);
        return duenioRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
