package com.owo.TP_prg3.Clases.InventarioPuesto.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioPuestoRepositorio extends JpaRepository<InventarioPuesto, Long> {
}
