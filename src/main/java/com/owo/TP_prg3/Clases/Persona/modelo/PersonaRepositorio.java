package com.owo.TP_prg3.Clases.Persona.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepositorio extends JpaRepository<Persona, Long> {
    Optional<Persona> findByDni(Long dni);
}
