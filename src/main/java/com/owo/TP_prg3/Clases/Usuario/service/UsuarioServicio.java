package com.owo.TP_prg3.Clases.Usuario.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Usuario.dto.FormUsuarioDTO;
import com.owo.TP_prg3.Clases.Usuario.dto.UsuarioDTO;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.UsuarioRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UsuarioServicio implements I_CRUD<Usuario, UsuarioDTO, FormUsuarioDTO> {

    private static final String ENTIDAD = "Usuario";

    //Atributos
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Conversion
    @Override
    public Usuario convertir_a_Obj(FormUsuarioDTO fDTO) {
        validarDatosUsuario(fDTO);
        return new Usuario(
            null,
            fDTO.getEmail(),
            passwordEncoder.encode(fDTO.getContraseña()),
            fDTO.getRol()
        );
    }

    @Override
    public UsuarioDTO convertir_a_DTO(Usuario usuario){
        return new UsuarioDTO(
            usuario.getUsuarioId(),
            usuario.getEmail(),
            usuario.getRol().name()
        );
    }

    //Metodos'
    @Override
    public boolean cargar(FormUsuarioDTO dto) {
        validarDatosUsuario(dto);
        validarUsuarioNoExiste(dto.getEmail());

        Usuario usuario = convertir_a_Obj(dto);
        usuarioRepositorio.save(usuario);
        return true;
    }

    @Override
    public Set<UsuarioDTO> obtenerTodos() {
        return usuarioRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<UsuarioDTO> buscarPorID(Long id) {
        validarIdValido(id);
        return usuarioRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<UsuarioDTO> filtrar(String campo, Object valor) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de filtrado"); 
        }
        if (valor == null) {
            throw new CampoRequeridoException("valor de filtrado"); 
        }
        Stream<Usuario> stream = usuarioRepositorio.findAll().stream();
        
        Predicate<Usuario> filtro;
        String campoTrim = campo.toLowerCase().trim();
        switch (campoTrim) {
            case "rol"-> filtro = u -> u.getRol().name().equalsIgnoreCase(valor.toString().trim()); 
            case "email"-> filtro = u -> u.getEmail().equalsIgnoreCase(valor.toString().trim()); 
            default -> throw new IngresoInvalidoException( 
                "campo de filtrado",
                "debe ser 'rol' o 'email'"
            ); 
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<UsuarioDTO> ordenar(String campo, boolean ascendente) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de ordenamiento"); 
        }
        Stream<Usuario> stream = usuarioRepositorio.findAll().stream();

        Comparator<Usuario> comparador;
        switch (campo.toLowerCase()) {
            case "rol"-> comparador = Comparator.comparing(Usuario::getRol);
            default -> comparador = Comparator.comparing(Usuario::getUsuarioId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormUsuarioDTO updateDTO) {
        // 1. Validar el DTO
        validarDatosUsuario(updateDTO); 

        // 2. Obtener la entidad o lanzar EntidadNoEncontradaException
        Usuario usuario = obtenerUsuarioPorId(id); 
        
        // 3. Validar duplicados en OTRAS entidades (por Email)
        validarUsuarioNoExisteOtro(updateDTO.getEmail(), id); 

        // 4. Actualizar campos
        usuario.setEmail(updateDTO.getEmail().trim()); 
        usuario.setContraseña(passwordEncoder.encode(updateDTO.getContraseña().trim())); 
        usuario.setRol(updateDTO.getRol());

        usuarioRepositorio.save(usuario);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id); 
        usuarioRepositorio.delete(usuario);
        return true;
    }

    // VALIDACIONES PRIVADAS ===========================================================================================

    /**
     * @param id El ID a validar.
     * @throws IngresoInvalidoException si el ID es nulo o no positivo.
     */
    private void validarIdValido(Long id) { 
        if (id == null || id <= 0) {
            throw new IngresoInvalidoException("ID", "debe ser un número positivo para la entidad " + ENTIDAD);
        }
    }

    /**
     * @param dto El DTO a validar.
     * @throws IngresoInvalidoException si el DTO es nulo o si el email tiene formato inválido.
     * @throws CampoRequeridoException si algún campo obligatorio es nulo o vacío.
     */
    private void validarDatosUsuario(FormUsuarioDTO dto) { 
        if (dto == null) {
            throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");
        }
        
        // 1. Validar Email
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) { 
            throw new CampoRequeridoException("email");
        }
        // Validación de formato de Email
        String emailTrimmed = dto.getEmail().trim();
        if (!emailTrimmed.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
             throw new IngresoInvalidoException("email", "formato inválido: " + emailTrimmed); 
        }

        // 2. Validar Contraseña
        if (dto.getContraseña() == null || dto.getContraseña().trim().isEmpty()) { 
            throw new CampoRequeridoException("contraseña");
        }

        // 3. Validar Rol
        if (dto.getRol() == null) { 
            throw new CampoRequeridoException("rol");
        }
    }

    /**
     * @param email El email a verificar.
     * @throws EntidadDuplicadaException si ya existe un Usuario con ese email.
     */
    private void validarUsuarioNoExiste(String email) { 
        Optional<Usuario> existente = usuarioRepositorio.findByEmail(email.trim());
        if (existente.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }
    
    /**
     * @param email El email a verificar.
     * @param id El ID del usuario que se está actualizando.
     * @throws EntidadDuplicadaException si ya existe otro Usuario con ese email.
     */
    private void validarUsuarioNoExisteOtro(String email, Long id) { 
        Optional<Usuario> existente = usuarioRepositorio.findByEmail(email.trim());
        if (existente.isPresent() && !existente.get().getUsuarioId().equals(id)) {
            throw new EntidadDuplicadaException(ENTIDAD, "email", email.trim());
        }
    }

    /**
     * @param id El ID de la entidad a buscar.
     * @return La entidad Usuario encontrada.
     * @throws EntidadNoEncontradaException si no se encuentra la entidad.
     */
    private Usuario obtenerUsuarioPorId(Long id) { 
        validarIdValido(id); 
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id)); 
    }
}