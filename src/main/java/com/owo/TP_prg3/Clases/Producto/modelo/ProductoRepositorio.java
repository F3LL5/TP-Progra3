package com.owo.TP_prg3.Clases.Producto.modelo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto,Long> {

    Optional<Producto> findByNombreAndCategoria(String nombre, String categoria);
}
