package com.owo.TP_prg3.Clases.Duenio.modelo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuenioRepositorio extends JpaRepository<Duenio, Long> {
    Optional<Duenio> findByPersona_Dni(int dni);

    Optional<Duenio> findByUsuario_Email(String trim);

    boolean existsByUsuarioUsuarioId(Long usuarioId);
}
