package com.owo.TP_prg3.Clases.Usuario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.UsuarioRepositorio;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // El "username" que recibimos es el email.
        Usuario usuario = usuarioRepositorio.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con email: " + email));

        // Construimos el UserDetails de Spring Security.
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getContraseña())
                .roles(usuario.getRol().name().replace("ROLE_", ""))
                .build();
    }
}