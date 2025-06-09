package com.owo.TP_prg3.Clases.Entidad.controlador;

import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.service.EntidadServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entidades")
public class EntidadControlador {

    @Autowired
    private EntidadServicioImpl entidadServicio;

    @GetMapping
    public ResponseEntity<List<EntidadDTO>> getAllEntidades(){
        List<EntidadDTO> entidades = entidadServicio.getAllEntidades();
        return ResponseEntity.ok(entidades);
    }

    @GetMapping("/{id}")
    public EntidadDTO getEntidadById(@PathVariable Long id){
        return entidadServicio.getEntidadById(id).orElse(null);
    }

    @PostMapping
    public EntidadDTO createEntidad(@Valid @RequestBody CreateEntidadDTO createEntidadDTO){
        return entidadServicio.createEntidad(createEntidadDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteEntidad(@PathVariable Long id){
        return entidadServicio.deleteEntidad(id);
    }

    @PatchMapping("/{id}")
    public Optional<EntidadDTO> updateEntidad(@PathVariable Long id, @Valid @RequestBody UpdateEntidadDTO updateEntidadDTO){
        return entidadServicio.updateEntidad(id, updateEntidadDTO);
    }
}