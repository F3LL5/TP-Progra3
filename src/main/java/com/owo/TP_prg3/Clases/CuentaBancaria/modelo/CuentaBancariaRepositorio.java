package com.owo.TP_prg3.Clases.CuentaBancaria.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaBancariaRepositorio extends JpaRepository<CuentaBancaria, Long> {

}
