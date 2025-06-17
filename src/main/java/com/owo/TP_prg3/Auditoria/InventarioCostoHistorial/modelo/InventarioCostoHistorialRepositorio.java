package com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioCostoHistorialRepositorio extends JpaRepository<InventarioCostoHistorial,Long> {
}
