package com.owo.TP_prg3.Clases.Cliente.modelo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente,Long> {

    Optional<Cliente> findByPersona_Dni(Long dni);
}
