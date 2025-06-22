package com.owo.TP_prg3.Clases.Transaccion.service;

import com.owo.TP_prg3.Clases.Transaccion.dto.CreateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.UpdateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransaccionServicio {
    List<TransaccionDTO> getAllTransacciones();
    Optional<TransaccionDTO> getTransaccionById(Long id);
    List<TransaccionDTO> filtrarYOrdenarTransaccionesByPuestoId(Long puestoId, String tipo_transaccion, String sortBy, String sortDir);
    List<TransaccionDTO> filtrarYOrdenarTransaccionesByCuentaBancariaId(
            Long cuentaBancariaId,
            String tipo_transaccion,
            String sortBy,
            String sortDir
    );
    TransaccionDTO createTransaccion(CreateTransaccionDTO createTransaccionDTO);
    Optional<TransaccionDTO> updateTransaccion(Long id, UpdateTransaccionDTO updateTransaccionDTO);
    boolean deleteTransaccion(Long id);
    String listado();
    List<TransaccionDTO> getTransaccionesByPuestoId(Long puestoId);
    Optional<TransaccionDTO> getTransaccionByIdAndPuestoId(Long id, Long puestoId);
    List<TransaccionDTO> getTransaccionesByCuentaBancariaId(Long cuentaBancariaId);
    List<TransaccionDTO> filtrarYOrdenar(String tipo_transaccion, String sortBy, String sortDir);

    @Transactional
    void ajustarSaldosPorCambioDeMonto(Long transaccionId, BigDecimal previousMonto, BigDecimal newMonto);

    @Transactional
    void revertirMovimientoDeFondos(Transaccion transaccion);
}
