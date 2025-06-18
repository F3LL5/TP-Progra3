package com.owo.TP_prg3.Clases.CuentaBancaria.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CreateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.UpdateCuentaBancariaDTO;

import java.util.List;
import java.util.Optional;

public interface CuentaBancariaServicio {
    List<CuentaBancariaDTO> getAllCuentasBancarias();
    Optional<CuentaBancariaDTO> getCuentaBancariaById(Long id);
    CuentaBancariaDTO createCuentaBancaria(CreateCuentaBancariaDTO createCuentaBancariaDTO);
    Optional<CuentaBancariaDTO> updateCuentaBancaria(Long id, UpdateCuentaBancariaDTO updateCuentaBancariaDTO);
    boolean deleteCuentaBancaria(Long id);
}
