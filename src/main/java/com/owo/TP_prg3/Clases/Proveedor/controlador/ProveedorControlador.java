package com.owo.TP_prg3.Clases.Proveedor.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Proveedor.dto.FormProveedorDTO;
import com.owo.TP_prg3.Clases.Proveedor.dto.ProveedorDTO;
import com.owo.TP_prg3.Clases.Proveedor.service.ProveedorServicio;
import java.util.Set;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorControlador implements I_Controlador<ProveedorDTO, FormProveedorDTO> {

    @Autowired
    private ProveedorServicio proveedorServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<ProveedorDTO>> obtenerTodos(){
        return ResponseEntity.ok(proveedorServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<ProveedorDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(proveedorServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<ProveedorDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(proveedorServicio.ordenar(campo, ascendente));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=proveedores.xlsx");
        return ResponseEntity.ok().headers(headers).body(proveedorServicio.exportar());
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormProveedorDTO dto) {
        boolean exito = proveedorServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormProveedorDTO dto) {
        return ResponseEntity.ok(proveedorServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorServicio.eliminar(id)); 
    }
}
