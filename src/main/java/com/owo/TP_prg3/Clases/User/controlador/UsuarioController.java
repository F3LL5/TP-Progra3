package com.owo.TP_prg3.Clases.User.controlador;

import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.User.dto.CreateUsuarioDTO;
import com.owo.TP_prg3.Clases.User.dto.UsuarioDTO;
import com.owo.TP_prg3.Clases.User.modelo.Usuario;
import com.owo.TP_prg3.Clases.User.service.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody CreateUsuarioDTO createUsuarioDTO) {
        try {
            usuarioServicio.crearUsuario(createUsuarioDTO);
            return ResponseEntity.ok("Usuario creado exitosamente para el DNI: " + createUsuarioDTO.getDni());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<List<UsuarioDTO>> getAllUsers(){
        List<UsuarioDTO> items = usuarioServicio.getAllUsers();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/listado")
    public String obtenerTodosString(){
        return usuarioServicio.listado();
    }
}