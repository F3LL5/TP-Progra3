package com.owo.TP_prg3.Clases.Inventario.controlador;

import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Inventario.dto.FormInventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.dto.InventarioDTO;
import com.owo.TP_prg3.Clases.Inventario.service.InventarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/inventarios")
public class InventarioControlador implements I_Controlador<InventarioDTO, FormInventarioDTO>{

    @Autowired
    private InventarioService inventarioServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<InventarioDTO>> obtenerTodos(){
        return ResponseEntity.ok(inventarioServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> buscarPorID(@PathVariable Long id){
        return ResponseEntity.ok(inventarioServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<InventarioDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(inventarioServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<InventarioDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(inventarioServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormInventarioDTO dto){
        boolean exito = inventarioServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true);
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id){
        return ResponseEntity.ok(inventarioServicio.eliminar(id));
    }

    // --- Métodos PUT ---
    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormInventarioDTO dto){
        return ResponseEntity.ok(inventarioServicio.actualizar(id, dto));
    }

}
