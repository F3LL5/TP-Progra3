package com.owo.TP_prg3.Clases.Puesto.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuestoRepositorio extends JpaRepository<Puesto, Long> {
}
