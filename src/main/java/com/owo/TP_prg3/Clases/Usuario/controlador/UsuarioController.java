package com.owo.TP_prg3.Clases.Usuario.controlador;

import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Usuario.dto.FormUsuarioDTO;
import com.owo.TP_prg3.Clases.Usuario.dto.UsuarioDTO;
import com.owo.TP_prg3.Clases.Usuario.service.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController implements I_Controlador<UsuarioDTO, FormUsuarioDTO> {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Override
    @GetMapping
    public ResponseEntity<Set<UsuarioDTO>> obtenerTodos(){
        return ResponseEntity.ok(usuarioServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<UsuarioDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(usuarioServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<UsuarioDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(usuarioServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormUsuarioDTO dto) {
        boolean exito = usuarioServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioServicio.eliminar(id)); 
    }
}