package com.owo.TP_prg3.Clases.Entidad.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntidadRepositorio extends JpaRepository<Entidad, Long> {
}
