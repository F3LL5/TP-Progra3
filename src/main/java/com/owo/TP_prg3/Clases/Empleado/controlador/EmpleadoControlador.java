package com.owo.TP_prg3.Clases.Empleado.controlador;

import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Empleado.dto.EmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.dto.FormEmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.service.EmpleadoServicio;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoControlador implements I_Controlador<EmpleadoDTO, FormEmpleadoDTO> {

    @Autowired
    private EmpleadoServicio empleadoServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<EmpleadoDTO>> obtenerTodos(){
        return ResponseEntity.ok(empleadoServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoServicio.buscarPorID(id).orElse(null));
    }

    @GetMapping("/buscarXEmail/{email}")
    public ResponseEntity<EmpleadoDTO> buscarPorEmail(@PathVariable String email) {
        return empleadoServicio.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<EmpleadoDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(empleadoServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<EmpleadoDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(empleadoServicio.ordenar(campo, ascendente));
    }

     @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=proveedores.xlsx");
        return ResponseEntity.ok().headers(headers).body(empleadoServicio.exportar());
    }


    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormEmpleadoDTO dto) {
        boolean exito = empleadoServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormEmpleadoDTO dto) {
        return ResponseEntity.ok(empleadoServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoServicio.eliminar(id)); 
    }
}
