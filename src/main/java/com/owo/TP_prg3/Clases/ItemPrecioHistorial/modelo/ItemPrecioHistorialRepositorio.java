package com.owo.TP_prg3.Clases.ItemPrecioHistorial.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPrecioHistorialRepositorio extends JpaRepository<ItemPrecioHistorial,Long> {
}
