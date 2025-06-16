package com.owo.TP_prg3.Clases.CuentaBancaria.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaBancariaRepositorio extends JpaRepository<CuentaBancaria, Long> {
    /// NO ANDA MUY BIEN porque (creo) que el atributo entidad_id debería escribirse entidadId y no lo detecta bien
//    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CuentaBancaria c WHERE c.entidad.entidad_id = :entidadId")
//    boolean existsByEntidadId(@Param("entidad_id") Long entidadId);
}
