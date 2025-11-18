package com.owo.TP_prg3.Clases.Empleado.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Empleado.dto.EmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.dto.FormEmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.modelo.Empleado;
import com.owo.TP_prg3.Clases.Empleado.modelo.EmpleadoRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Usuario.modelo.RolUsuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;

@Service
public class EmpleadoServicio implements I_CRUD<Empleado, EmpleadoDTO, FormEmpleadoDTO> {

    private static final String ENTIDAD = "Empleado";

    @Autowired
    private EmpleadoRepositorio empleadoRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Conversion
    @Override
    public Empleado convertir_a_Obj(FormEmpleadoDTO fDTO) {
        validarDatosEmpleado(fDTO);
        Empleado empleado = new Empleado();
        empleado.setNombre(fDTO.getNombre());
        empleado.setEdad(fDTO.getEdad());
        empleado.setDni(fDTO.getDni());
        Usuario usuario = new Usuario(null, fDTO.getEmail(), passwordEncoder.encode(fDTO.getContraseña()), RolUsuario.ROLE_EMPLEADO);
        empleado.setUsuario(usuario);
        return empleado;
    }

    @Override
    public EmpleadoDTO convertir_a_DTO(Empleado empleado) {
        return new EmpleadoDTO(
            empleado.getPersonaId(), 
            empleado.getDni(),
            empleado.getNombre(),
            empleado.getEdad(),
            empleado.getEmpleadoId(),
            empleado.getUsuario().getEmail()
        );
    }

    @Override
    public Set<EmpleadoDTO> obtenerTodos() {
        return empleadoRepositorio.findAll().stream() // JPA automáticamente hace el JOIN entre las tablas 'personas' y 'empleados'.
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<EmpleadoDTO> buscarPorID(Long id) {
        validarIdValido(id);
        return empleadoRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<EmpleadoDTO> buscarPorDNI(int dni) {
        if (dni <= 0) {
             throw new IngresoInvalidoException("DNI", "debe ser un número positivo.");
        }
        return empleadoRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<EmpleadoDTO> filtrar(String campo, Object valor) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de filtrado");
        }
        if (valor == null) {
            throw new CampoRequeridoException("valor de filtrado");
        }
        Stream<Empleado> stream = empleadoRepositorio.findAll().stream();
        Predicate<Empleado> filtro;
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

    public Set<EmpleadoDTO> ordenar(String campo, boolean ascendente) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de ordenamiento");
        }
        Stream<Empleado> stream = empleadoRepositorio.findAll().stream();

        Comparator<Empleado> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Empleado::getNombre);
            case "edad"-> comparador = Comparator.comparing(Empleado::getEdad);
            case "dni"-> comparador = Comparator.comparing(Empleado::getDni);
            default -> comparador = Comparator.comparing(Empleado::getEmpleadoId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    public boolean cargar(FormEmpleadoDTO createDTO) {
        validarDatosEmpleado(createDTO);
        validarEmpleadoNoExiste(createDTO.getDni(), createDTO.getEmail());
        empleadoRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormEmpleadoDTO updateDTO) {
        validarDatosEmpleado(updateDTO); 
        Empleado empleado = obtenerEmpleadoPorId(id); 
        validarEmpleadoNoExisteOtro(updateDTO.getDni(), updateDTO.getEmail(), id);

        // Actualizar campos
        empleado.setNombre(updateDTO.getNombre().trim()); 
        empleado.setEdad(updateDTO.getEdad());
        empleado.setDni(updateDTO.getDni());
        // Actualizar datos del Usuario asociado
        empleado.getUsuario().setEmail(updateDTO.getEmail().trim()); 
        empleado.getUsuario().setContraseña(passwordEncoder.encode(updateDTO.getContraseña().trim())); 
        
        empleadoRepositorio.save(empleado);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Optional<Empleado> optional = empleadoRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else empleadoRepositorio.delete(optional.get());
        return true;
    }

    // VALIDACIONES PRIVADAS ===========================================================================================

    private void validarIdValido(Long id) { 
        if (id == null || id <= 0) {
            throw new IngresoInvalidoException("ID", "debe ser un número positivo para la entidad " + ENTIDAD);
        }
    }

    private void validarDatosEmpleado(FormEmpleadoDTO dto) { 
        if (dto == null) {
            throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");
        }
        
        // 1. Validar Nombre (String)
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) { // Usar .trim()
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

    private void validarEmpleadoNoExiste(int dni, String email) { 
        Optional<EmpleadoDTO> existenteDni = this.buscarPorDNI(dni); 
        if (existenteDni.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "DNI", dni);
        }
        
        Optional<Empleado> existenteEmail = empleadoRepositorio.findByUsuario_Email(email.trim());
        if (existenteEmail.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }
    
    private void validarEmpleadoNoExisteOtro(int dni, String email, Long id) {
        // 1. Check DNI
        Optional<Empleado> existenteDni = empleadoRepositorio.findByPersona_Dni(dni);
        // Si existe y su EmpleadoId es diferente, es duplicado.
        if (existenteDni.isPresent() && !existenteDni.get().getEmpleadoId().equals(id)) {
            throw new EntidadDuplicadaException(ENTIDAD, "DNI", dni);
        }
        
        // 2. Check Email
        Optional<Empleado> existenteEmail = empleadoRepositorio.findByUsuario_Email(email.trim());
        if (existenteEmail.isPresent() && !existenteEmail.get().getEmpleadoId().equals(id)) {
             throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }

    private Empleado obtenerEmpleadoPorId(Long id) { 
        validarIdValido(id);
        return empleadoRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
