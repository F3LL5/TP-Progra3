package com.owo.TP_prg3.Clases.Empleado.modelo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepositorio extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findByPersona_Dni(int dni);
}
