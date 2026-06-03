package com.owo.TP_prg3.Clases.CuentaBancaria.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/cuentas-bancarias")
public class HistorialCuentaBancariaControlador {

    private final HistorialCuentaBancariaServicio servicio;

    public HistorialCuentaBancariaControlador(HistorialCuentaBancariaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialCuentaBancaria> global() {
        return servicio.obtenerTodo();
    }
}
