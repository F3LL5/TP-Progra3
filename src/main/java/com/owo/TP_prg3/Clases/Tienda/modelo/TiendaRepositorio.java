package com.owo.TP_prg3.Clases.Tienda.modelo;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TiendaRepositorio extends JpaRepository<Tienda, Long> {

    Optional<Tienda> findByRazonSocial(String razonSocial);

    Optional<Tienda> findByDuenio_DuenioId(Long duenioId);

    boolean existsByDuenio_DuenioId(Long duenioId);

    @Query("SELECT t.ultimoCierreCaja FROM Tienda t WHERE t.tiendaId = :tiendaId")
    Optional<LocalDate> findUltimoCierreCajaById(@Param("tiendaId") Long tiendaId);
}
