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
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;

@Service
public class DuenioServicio implements I_CRUD<Duenio, DuenioDTO, FormDuenioDTO> {

    private static final String ENTIDAD = "Dueño";

    @Autowired
    private DuenioRepositorio duenioRepositorio;

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
        validarIdValido(id);
        return duenioRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<DuenioDTO> buscarPorDNI(int dni) {
        if (dni <= 0) {
             throw new IngresoInvalidoException("DNI", "debe ser un número positivo.");
        }
        return duenioRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
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
    public boolean cargar(FormDuenioDTO createDTO) {
        validarDatosDuenio(createDTO);
        validarDuenioNoExiste(createDTO.getDni(), createDTO.getEmail());
        duenioRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormDuenioDTO updateDTO) {
        validarDatosDuenio(updateDTO); 

        Duenio duenio = obtenerDuenioPorId(id);
        
        validarDuenioNoExisteOtro(updateDTO.getDni(), updateDTO.getEmail(), id);

        // Actualizar campos
        duenio.setNombre(updateDTO.getNombre().trim()); 
        duenio.setEdad(updateDTO.getEdad());
        duenio.setDni(updateDTO.getDni());
        duenio.getUsuario().setEmail(updateDTO.getEmail().trim()); 
        duenio.getUsuario().setContraseña(passwordEncoder.encode(updateDTO.getContraseña().trim())); 

        duenioRepositorio.save(duenio);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Duenio duenio = obtenerDuenioPorId(id);
        duenioRepositorio.delete(duenio);
        return true;
    }

    // VALIDACIONES PRIVADAS ===========================================================================================

    private void validarIdValido(Long id) {
        if (id == null || id <= 0) {
            throw new IngresoInvalidoException("ID", "debe ser un número positivo para la entidad " + ENTIDAD);
        }
    }

    private void validarDatosDuenio(FormDuenioDTO dto) {
        if (dto == null) {
            throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");
        }
        
        // 1. Validar Nombre (String)
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) { 
            throw new CampoRequeridoException("nombre");
        }
        // 2. Validar Edad (Integer > 0)
        if (dto.getEdad() == null || dto.getEdad() <= 0) { 
            throw new CampoRequeridoException("edad"); 
        }
        // 3. Validar DNI (int > 0)
        if (dto.getDni() <= 0) { 
             throw new IngresoInvalidoException("DNI", "debe ser un número positivo.");
        }
        // 4. Validar Email (String, requerido y formato)
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new CampoRequeridoException("email");
        }
        String emailTrimmed = dto.getEmail().trim();
        if (!emailTrimmed.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) { 
             throw new IngresoInvalidoException("email", "formato inválido: " + emailTrimmed);
        }
        // 5. Validar Contraseña (String)
        if (dto.getContraseña() == null || dto.getContraseña().trim().isEmpty()) {
            throw new CampoRequeridoException("contraseña");
        }
    }

    private void validarDuenioNoExiste(int dni, String email) { 
        Optional<DuenioDTO> existenteDni = this.buscarPorDNI(dni); 
        if (existenteDni.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "DNI", dni);
        }
        
        Optional<Duenio> existenteEmail = duenioRepositorio.findByUsuario_Email(email.trim());
        if (existenteEmail.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }
    
    private void validarDuenioNoExisteOtro(int dni, String email, Long id) { 
        Optional<Duenio> existenteDni = duenioRepositorio.findByPersona_Dni(dni);
        // Si existe y su DuenioId es diferente, es duplicado.
        if (existenteDni.isPresent() && !existenteDni.get().getDuenioId().equals(id)) {
            throw new EntidadDuplicadaException(ENTIDAD, "DNI", dni);
        }
        
        // 2. Check Email
        Optional<Duenio> existenteEmail = duenioRepositorio.findByUsuario_Email(email.trim());
        if (existenteEmail.isPresent() && !existenteEmail.get().getDuenioId().equals(id)) {
             throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }

    private Duenio obtenerDuenioPorId(Long id) { 
        validarIdValido(id);
        return duenioRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
