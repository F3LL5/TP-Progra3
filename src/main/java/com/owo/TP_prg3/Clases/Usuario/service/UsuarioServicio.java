package com.owo.TP_prg3.Clases.Usuario.service;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Usuario.dto.FormUsuarioDTO;
import com.owo.TP_prg3.Clases.Usuario.dto.UsuarioDTO;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.UsuarioRepositorio;
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

    //Atributos
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Conversion
    @Override
    public Usuario convertir_a_Obj(FormUsuarioDTO fDTO) {
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
        if (usuarioRepositorio.findByEmail(dto.getEmail()).isPresent()) return false;

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
        return usuarioRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public Set<UsuarioDTO> filtrar(String campo, Object valor) {
        Stream<Usuario> stream = usuarioRepositorio.findAll().stream();

        Predicate<Usuario> filtro;
        switch (campo.toLowerCase()) {
            case "rol"-> filtro = u -> u.getRol().equals(valor);
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<UsuarioDTO> ordenar(String campo, boolean ascendente) {
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
        Optional<Usuario> optional = usuarioRepositorio.findById(id);
        if (optional.isEmpty()) return false;

        Usuario usuario = optional.get();
        usuario.setEmail(updateDTO.getEmail());
        usuario.setContraseña(passwordEncoder.encode(updateDTO.getContraseña()));
        usuario.setRol(updateDTO.getRol());

        usuarioRepositorio.save(usuario);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Optional<Usuario> optional = usuarioRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else usuarioRepositorio.delete(optional.get());
        return true;
    }

}