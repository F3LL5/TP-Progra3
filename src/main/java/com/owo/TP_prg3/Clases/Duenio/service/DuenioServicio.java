package com.owo.TP_prg3.Clases.Duenio.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Duenio.dto.DuenioDTO;
import com.owo.TP_prg3.Clases.Duenio.dto.FormDuenioDTO;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Duenio.modelo.DuenioRepositorio;
import com.owo.TP_prg3.Clases.Enum.RolUsuario;
import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
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
import org.springframework.data.domain.Sort;

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

    @Autowired
    private ExcelExportService excelExportService;

    // Conversion
    @Override
    public Duenio convertir_a_Obj(FormDuenioDTO fDTO) {
        validarDatosDuenio(fDTO);

        Duenio duenio = new Duenio();
        duenio.setPersona(new Persona());
        duenio.setNombre(fDTO.getNombre());
        duenio.setApellido(fDTO.getApellido());
        duenio.setDni(fDTO.getDni());
        duenio.setFechaNacimiento(fDTO.getFechaNacimiento());

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
            duenio.getApellido(),
            duenio.getFechaNacimiento(),
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

    public Optional<DuenioDTO> buscarPorDNI(Long dni) {
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

    
    public byte[] exportar() {
        List<DuenioDTO> lista = duenioRepositorio.findAll(Sort.by(Sort.Direction.ASC, "duenioId"))
            .stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toList());
        byte[] excel = excelExportService.generarExcel(lista, "Duenios");
        return excel;
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
        
        duenio.setNombre(updateDTO.getNombre().trim());
        duenio.setApellido(updateDTO.getApellido().trim());
        duenio.setDni(updateDTO.getDni());
        duenio.setFechaNacimiento(updateDTO.getFechaNacimiento());
        
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
        personaServicio.validarDatosPersona(new FormPersonaDTO(dto.getNombre(), dto.getApellido(), dto.getFechaNacimiento(), dto.getDni()));
        
        // El nombre y apellido del dueño no pueden contener números ni caracteres especiales
        ValidacionGeneral.soloLetras(dto.getNombre(), "nombre");
        ValidacionGeneral.soloLetras(dto.getApellido(), "apellido");
        
        // El dueño debe ser mayor o igual a 16 años
        ValidacionGeneral.validarEdad(dto.getFechaNacimiento(), 16, "fechaNacimiento");
        
        // Validaciones de Usuario
        if (dto.getEmail() == null) throw new CampoRequeridoException("email");
        if (dto.getContraseña() == null) throw new CampoRequeridoException("contrasenia");
        
        ValidacionGeneral.validarEmailFormat(dto.getEmail(), "email");
        ValidacionGeneral.validarContrasenia(dto.getContraseña(), "contrasenia");
    }

    private void validarDuenioNoExiste(Long dni, String email) { 
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

    @Override
    public Set<DuenioDTO> filtrar(String campo, Object valor) {
        throw new UnsupportedOperationException("Unimplemented method 'filtrar'");
    }

    @Override
    public Set<DuenioDTO> ordenar(String campo, boolean ascendente) {
        throw new UnsupportedOperationException("Unimplemented method 'ordenar'");
    }
}
