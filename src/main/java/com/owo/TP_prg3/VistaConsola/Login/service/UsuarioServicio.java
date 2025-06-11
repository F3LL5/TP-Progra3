package com.owo.TP_prg3.VistaConsola.Login.service;


import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.TipoEntidad;
import com.owo.TP_prg3.VistaConsola.Login.dto.CreateUsuarioDTO;
import com.owo.TP_prg3.VistaConsola.Login.modelo.RolUsuario;
import com.owo.TP_prg3.VistaConsola.Login.modelo.Usuario;
import com.owo.TP_prg3.VistaConsola.Login.modelo.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EntidadRepositorio entidadRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario crearUsuario(CreateUsuarioDTO dto) {
        // 1. Verificar que la entidad exista
        Entidad entidadAsociada = entidadRepositorio.findByDni(dto.getDni())
                .orElseThrow(() -> new RuntimeException("No existe una entidad con el DNI proporcionado."));

        // 2. Verificar que la entidad tenga un tipo válido para crear un usuario
        TipoEntidad tipo = entidadAsociada.getTipoEntidad();
        if (tipo != TipoEntidad.DUENO_PUESTO && tipo != TipoEntidad.ADMIN) {
            throw new RuntimeException("No se puede crear un usuario para una entidad de tipo " + tipo);
        }

        // 3. Verificar que no exista ya un usuario con ese DNI
        if (usuarioRepositorio.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe un usuario con ese DNI.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(dto.getDni());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Codificar contraseña
        nuevoUsuario.setEntidad(entidadAsociada);

        // 4. Asignar rol basado en el tipo de entidad
        if (tipo == TipoEntidad.ADMIN) {
            nuevoUsuario.setRol(RolUsuario.ROLE_ADMIN);
        } else {
            nuevoUsuario.setRol(RolUsuario.ROLE_DUENO_PUESTO);
        }

        return usuarioRepositorio.save(nuevoUsuario);
    }
}