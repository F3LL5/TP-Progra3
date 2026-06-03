package com.owo.TP_prg3.Clases.CuentaBancaria.historial;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialCuentaBancariaServicio {

    private final HistorialCuentaBancariaRepositorio repositorio;

    public HistorialCuentaBancariaServicio(HistorialCuentaBancariaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<HistorialCuentaBancaria> obtenerTodo() {
        return repositorio.findAllByOrderByFechaEventoDesc();
    }
}
