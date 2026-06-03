package com.owo.TP_prg3.Clases.Empleado.historial;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial/empleados")
public class HistorialEmpleadoControlador {

    private final HistorialEmpleadoServicio servicio;

    public HistorialEmpleadoControlador(HistorialEmpleadoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<HistorialEmpleado> global() {
        return servicio.obtenerTodo();
    }
}
