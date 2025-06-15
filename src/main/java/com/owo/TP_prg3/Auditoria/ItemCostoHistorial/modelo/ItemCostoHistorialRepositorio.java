package com.owo.TP_prg3.Auditoria.ItemCostoHistorial.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCostoHistorialRepositorio extends JpaRepository<ItemCostoHistorial ,Long> {
}
