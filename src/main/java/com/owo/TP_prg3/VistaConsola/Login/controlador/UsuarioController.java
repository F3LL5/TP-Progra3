package com.owo.TP_prg3.VistaConsola.Login.controlador;

import com.owo.TP_prg3.VistaConsola.Login.dto.CreateUsuarioDTO;
import com.owo.TP_prg3.VistaConsola.Login.service.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}