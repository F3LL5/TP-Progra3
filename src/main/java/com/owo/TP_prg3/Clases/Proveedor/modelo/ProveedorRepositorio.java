package com.owo.TP_prg3.Clases.Proveedor.modelo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor,Long> {

    Optional<Proveedor> findByCuit(Long cuit);

    boolean existsByCuit(Long cuit);

    List<Proveedor> findByNombreFantasiaContainingIgnoreCase(String nombre);
}
