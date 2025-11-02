package com.owo.TP_prg3.Clases.Inventario.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepositorio extends JpaRepository<Inventario, Long> {
}
