package com.owo.TP_prg3.Clases.Empleado.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Empleado.dto.EmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.dto.FormEmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.modelo.Empleado;
import com.owo.TP_prg3.Clases.Empleado.modelo.EmpleadoRepositorio;
import com.owo.TP_prg3.Clases.Enum.RolUsuario;
import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
import com.owo.TP_prg3.Clases.Persona.service.PersonaServicio;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;

@Service
public class EmpleadoServicio implements I_CRUD<Empleado, EmpleadoDTO, FormEmpleadoDTO> {

    private static final String ENTIDAD = "Empleado";

    @Autowired
    private EmpleadoRepositorio empleadoRepositorio;

    @Autowired
    private PersonaServicio personaServicio;
    @Autowired
    private PersonaRepositorio personaRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExcelExportService excelExportService;

    // Conversion
    @Override
    public Empleado convertir_a_Obj(FormEmpleadoDTO fDTO) {
        validarDatosEmpleado(fDTO);
        Empleado empleado = new Empleado();
        empleado.setNombre(fDTO.getNombre());
        empleado.setApellido(fDTO.getApellido());
        empleado.setFechaNacimiento(fDTO.getFechaNacimiento());
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
            empleado.getApellido(),
            empleado.getFechaNacimiento(),
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
        ValidacionGeneral.validarIdValido(id);
        return empleadoRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<EmpleadoDTO> buscarPorDNI(Long dni) {
        if (dni <= 0) {
             throw new IngresoInvalidoException("DNI", "debe ser un número positivo.");
        }
        return empleadoRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    public Optional<EmpleadoDTO> buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new CampoRequeridoException("email");
        }
        ValidacionGeneral.validarEmailFormat(email, "email");

        return empleadoRepositorio.findByUsuario_Email(email.trim())
                .map(this::convertir_a_DTO);
    }

    public byte[] exportar() {
        List<EmpleadoDTO> lista = empleadoRepositorio.findAll(Sort.by(Sort.Direction.ASC, "empleadoId"))
            .stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toList());
        byte[] excel = excelExportService.generarExcel(lista, "Empleados");
        return excel;
    }


    // POST
    @Override
    @Transactional
    public boolean cargar(FormEmpleadoDTO createDTO) {
        validarDatosEmpleado(createDTO);
        validarEmpleadoNoExiste(createDTO.getDni(), createDTO.getEmail());
        empleadoRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
    @Override
    @Transactional
    public boolean actualizar(Long id, FormEmpleadoDTO updateDTO) {
        validarDatosEmpleado(updateDTO);
        Empleado empleado = obtenerEmpleadoPorId(id);
        
        empleado.getPersona().setNombre(updateDTO.getNombre().trim());
        empleado.getPersona().setApellido(updateDTO.getApellido().trim());
        empleado.getPersona().setFechaNacimiento(updateDTO.getFechaNacimiento());
        empleado.getPersona().setDni(updateDTO.getDni());
        
        empleado.getUsuario().setEmail(updateDTO.getEmail().trim());
        if (updateDTO.getContraseña() != null && !updateDTO.getContraseña().trim().isEmpty()) {
            empleado.getUsuario().setContraseña(passwordEncoder.encode(updateDTO.getContraseña()));
        }
        
        empleadoRepositorio.save(empleado);
        return true;
    }

    // DELETE 
    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Empleado empleado = obtenerEmpleadoPorId(id);
        empleadoRepositorio.delete(empleado);
        return true;
    }

    // VALIDACIONES PRIVADAS ===========================================================================================


    private void validarDatosEmpleado(FormEmpleadoDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");

        // Validaciones de Persona
        personaServicio.validarDatosPersona(new FormPersonaDTO(dto.getNombre(), dto.getApellido(), dto.getFechaNacimiento(), dto.getDni()));
        
        // El empleado debe ser mayor o igual a 16 años
        ValidacionGeneral.validarEdad(dto.getFechaNacimiento(), 16, "fechaNacimiento");
        
        // Validaciones de Usuario
        if (dto.getEmail() == null) throw new CampoRequeridoException("email");
        if (dto.getContraseña() == null) throw new CampoRequeridoException("contrasenia");
        
        ValidacionGeneral.validarEmailFormat(dto.getEmail(), "email");
        ValidacionGeneral.validarContrasenia(dto.getContraseña(), "contrasenia");
    }

    private void validarEmpleadoNoExiste(Long dni, String email) { 
        Optional<EmpleadoDTO> existenteDni = this.buscarPorDNI(dni); 
        Optional<Persona> existeDniPersona = personaRepositorio.findByDni(dni);
        // Si existe y su EmpleadoId es diferente, es duplicado.
        if (existenteDni.isPresent() || existeDniPersona.isPresent() ) {
            throw new EntidadDuplicadaException(ENTIDAD, "DNI", dni);
        }
        
        Optional<Empleado> existenteEmail = empleadoRepositorio.findByUsuario_Email(email.trim());
        if (existenteEmail.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }

    private Empleado obtenerEmpleadoPorId(Long id) { 
        ValidacionGeneral.validarIdValido(id);
        return empleadoRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }

    @Override
    public Set<EmpleadoDTO> filtrar(String campo, Object valor) {
        throw new UnsupportedOperationException("Unimplemented method 'filtrar'");
    }

    @Override
    public Set<EmpleadoDTO> ordenar(String campo, boolean ascendente) {
        throw new UnsupportedOperationException("Unimplemented method 'ordenar'");
    }
}
