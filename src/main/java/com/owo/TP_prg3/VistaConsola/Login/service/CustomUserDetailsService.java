package com.owo.TP_prg3.VistaConsola.Login.service;

import com.owo.TP_prg3.VistaConsola.Login.modelo.Usuario;

import com.owo.TP_prg3.VistaConsola.Login.modelo.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // El "username" que recibimos es el DNI.
        Integer dni = Integer.parseInt(username);
        Usuario usuario = usuarioRepositorio.findByDni(dni)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con DNI: " + username));

        // Construimos el UserDetails de Spring Security.
        return User.builder()
                .username(usuario.getDni().toString())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name().replace("ROLE_", ""))
                .build();
    }
}