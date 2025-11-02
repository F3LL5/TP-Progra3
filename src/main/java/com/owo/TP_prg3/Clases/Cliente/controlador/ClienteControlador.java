package com.owo.TP_prg3.Clases.Cliente.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Cliente.dto.ClienteDTO;
import com.owo.TP_prg3.Clases.Cliente.service.ClienteServicio;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import java.util.Set;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador implements I_Controlador<ClienteDTO, FormPersonaDTO> {

    @Autowired
    private ClienteServicio clienteServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<ClienteDTO>> obtenerTodos(){
        return ResponseEntity.ok(clienteServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(clienteServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<ClienteDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(clienteServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<ClienteDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(clienteServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormPersonaDTO dto) {
        boolean exito = clienteServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormPersonaDTO dto) {
        return ResponseEntity.ok(clienteServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(clienteServicio.eliminar(id)); 
    }
}
