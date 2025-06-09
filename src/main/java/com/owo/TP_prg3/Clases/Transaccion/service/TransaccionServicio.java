package com.owo.TP_prg3.Clases.Transaccion.service;

import com.owo.TP_prg3.Clases.Transaccion.dto.CreateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.UpdateTransaccionDTO;

import java.util.List;
import java.util.Optional;

public interface TransaccionServicio {
    List<TransaccionDTO> getAllTransacciones();
    Optional<TransaccionDTO> getTransaccionById(Long id);
    TransaccionDTO createTransaccion(CreateTransaccionDTO createTransaccionDTO);
    Optional<TransaccionDTO> updateTransaccion(Long id, UpdateTransaccionDTO updateTransaccionDTO);
    boolean deleteTransaccion(Long id);
}
