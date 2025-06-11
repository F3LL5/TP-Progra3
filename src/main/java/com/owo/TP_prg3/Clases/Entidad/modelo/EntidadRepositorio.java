package com.owo.TP_prg3.Clases.Entidad.modelo;

import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntidadRepositorio extends JpaRepository<Entidad, Long> {
    Optional<Entidad> findByDni(int dni);
}
