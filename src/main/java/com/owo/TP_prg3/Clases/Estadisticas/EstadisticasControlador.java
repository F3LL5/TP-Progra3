package com.owo.TP_prg3.Clases.Estadisticas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats") @CrossOrigin(origins = "*")
public class EstadisticasControlador {

    @Autowired
    private EstadisticasServicio estadisticasServicio;

    @GetMapping("/dashboard")
    public DashboardDTO getDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        
        return estadisticasServicio.obtenerResumen(inicio, fin);
    }

    @GetMapping("/caja-diaria")
    public CierreCajaDTO getCajaDiaria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return estadisticasServicio.obtenerMovimientosCajaDiaria(fecha);
    }
}