package com.owo.TP_prg3.Clases.Persona.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Persona.dto.PersonaDTO;
import com.owo.TP_prg3.Clases.Persona.service.PersonaServicio;
import java.util.Set;

@RestController
@RequestMapping("/api/personas")
public class PersonaControlador implements I_Controlador<PersonaDTO, FormPersonaDTO>{

    @Autowired
    private PersonaServicio personaServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<PersonaDTO>> obtenerTodos(){
        return ResponseEntity.ok(personaServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(personaServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<PersonaDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(personaServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<PersonaDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(personaServicio.ordenar(campo, ascendente));
    }
     @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=proveedores.xlsx");
        return ResponseEntity.ok().headers(headers).body(personaServicio.exportar());
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormPersonaDTO dto) {
        boolean exito = personaServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormPersonaDTO dto) {
        return ResponseEntity.ok(personaServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(personaServicio.eliminar(id)); 
    }
}