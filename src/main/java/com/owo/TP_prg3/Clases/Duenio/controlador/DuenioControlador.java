package com.owo.TP_prg3.Clases.Duenio.controlador;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Duenio.dto.DuenioDTO;
import com.owo.TP_prg3.Clases.Duenio.dto.FormDuenioDTO;
import com.owo.TP_prg3.Clases.Duenio.service.DuenioServicio;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;

@RestController
@RequestMapping("/api/duenios")
public class DuenioControlador implements I_Controlador<DuenioDTO, FormDuenioDTO> {

    @Autowired
    private DuenioServicio duenioServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<DuenioDTO>> obtenerTodos(){
        return ResponseEntity.ok(duenioServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DuenioDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(duenioServicio.buscarPorID(id).orElse(null));
    }

    @GetMapping("/buscarXEmail/{email}")
    public ResponseEntity<DuenioDTO> buscarPorEmail(@PathVariable String email) {
        return duenioServicio.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<DuenioDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(duenioServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<DuenioDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(duenioServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormDuenioDTO dto) {
        boolean exito = duenioServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormDuenioDTO dto) {
        return ResponseEntity.ok(duenioServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(duenioServicio.eliminar(id)); 
    }
}
