package com.owo.TP_prg3.Clases.Tienda.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Tienda.dto.FormTiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.TiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.service.TiendaServicio;
import java.util.Set;

@RestController
@RequestMapping("/api/tiendas")
public class TiendaControlador implements I_Controlador<TiendaDTO, FormTiendaDTO> {

    @Autowired
    private TiendaServicio tiendaServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<TiendaDTO>> obtenerTodos(){
        Set<TiendaDTO> tiendas = tiendaServicio.obtenerTodos();
        return ResponseEntity.ok(tiendas);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TiendaDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(tiendaServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<TiendaDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(tiendaServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<TiendaDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(tiendaServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormTiendaDTO dto) {
        boolean exito = tiendaServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormTiendaDTO dto) {
        return ResponseEntity.ok(tiendaServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(tiendaServicio.eliminar(id)); 
    }
    
}