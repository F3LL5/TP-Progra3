package com.owo.TP_prg3.Clases.InventarioPuesto.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioPuestoRepositorio extends JpaRepository<InventarioPuesto, Long> {
    Optional<InventarioPuesto> findByPuesto_PuestoIdAndItemId(Long puestoId, Long itemId);
}
