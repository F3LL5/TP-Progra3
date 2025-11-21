package com.owo.TP_prg3.Clases.Tienda.modelo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiendaRepositorio extends JpaRepository<Tienda, Long> {

    Optional<Tienda> findByNombre(String trim);

}
