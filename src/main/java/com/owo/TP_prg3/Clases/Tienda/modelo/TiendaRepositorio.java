package com.owo.TP_prg3.Clases.Tienda.modelo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiendaRepositorio extends JpaRepository<Tienda, Long> {

    Optional<Tienda> findByRazonSocial(String razonSocial);

    Optional<Tienda> findByDuenio_DuenioId(Long duenioId);

    boolean existsByDuenio_DuenioId(Long duenioId);
    
}
