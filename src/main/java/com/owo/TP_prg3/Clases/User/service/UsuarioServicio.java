package com.owo.TP_prg3.Clases.User.service;


import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
import com.owo.TP_prg3.Clases.User.dto.CreateUsuarioDTO;
import com.owo.TP_prg3.Clases.User.dto.UsuarioDTO;
import com.owo.TP_prg3.Clases.User.modelo.RolUsuario;
import com.owo.TP_prg3.Clases.User.modelo.Usuario;
import com.owo.TP_prg3.Clases.User.modelo.UsuarioRepositorio;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServicio {

    //Atributos
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PersonaRepositorio entidadRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Conversion
    private UsuarioDTO convertirA_DTO(Usuario usuario){
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getRol(),
                usuario.getEntidad().getPersonaId(),
                usuario.getDni()
        );
    }

    //Metodos
    public Usuario crearUsuario(CreateUsuarioDTO dto) {
        // 1. Verificar que la entidad exista
        Persona entidadAsociada = entidadRepositorio.findByDni(dto.getDni())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una entidad con el DNI proporcionado."));

        // // 2. Verificar que la entidad tenga un tipo válido para crear un usuario
        // RolEntidad tipo = entidadAsociada.getRolEntidad();
        // if (tipo != RolEntidad.DUENO_PUESTO && tipo != RolEntidad.ADMIN) {
        //     throw new IngresoInvalidoException("No se puede crear un usuario para una entidad de tipo " + tipo);
        // }

        // 3. Verificar que no exista ya un usuario con ese DNI
        if (usuarioRepositorio.existsByDni(dto.getDni())) {
            throw new IngresoInvalidoException("Ya existe un usuario con ese DNI.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(dto.getDni());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Codificar contraseña
        nuevoUsuario.setEntidad(entidadAsociada);

        // // 4. Asignar rol basado en el rol de la entidad
        // if (tipo == RolEntidad.ADMIN) {
        //     nuevoUsuario.setRol(RolUsuario.ROLE_ADMIN);
        // } else {
        //     nuevoUsuario.setRol(RolUsuario.ROLE_DUENO_PUESTO);
        // }
        nuevoUsuario.setRol(RolUsuario.ROLE_ADMIN);

        return usuarioRepositorio.save(nuevoUsuario);
    }

    public List<UsuarioDTO> getAllUsers(){
        return usuarioRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }
}