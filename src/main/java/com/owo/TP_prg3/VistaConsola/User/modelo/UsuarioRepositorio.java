package com.owo.TP_prg3.VistaConsola.User.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDni(Integer dni);
    boolean existsByDni(Integer dni);
}