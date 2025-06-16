package com.owo.TP_prg3.Auditoria.DuenioHistorial.modelo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface DuenioHistorialRepositorio extends JpaRepository<DuenioHistorial,Long> {
}
