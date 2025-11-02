package com.owo.TP_prg3.Clases.Lote.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteRepositorio extends JpaRepository<Lote,Long> {
}
