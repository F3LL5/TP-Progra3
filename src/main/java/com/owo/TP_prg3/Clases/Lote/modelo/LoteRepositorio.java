package com.owo.TP_prg3.Clases.Lote.modelo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteRepositorio extends JpaRepository<Lote,Long> {

    // Consulta sql que busca los lotes de un producto especifico y tienen stock mayor a i
    List<Lote> findByProductoIdAndCantidadDisponibleGreaterThan(Long productoId, int i);

    // Consulta sql que hace lo mismo que la de arriba pero los ordena por fecha de ingreso
    List<Lote> findByProductoIdAndCantidadDisponibleGreaterThanOrderByFechaIngresoAsc(Long productoId, int i);
  
}
