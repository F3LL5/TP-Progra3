package com.owo.TP_prg3.Clases.Entidad.controlador;

import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.service.EntidadServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<EntidadDTO> getEntidadById(@PathVariable Long id) {
        EntidadDTO entidadDTO = entidadServicio.getEntidadById(id)
                .orElse(null);
        return ResponseEntity.ok(entidadDTO);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<EntidadDTO> getEntidadById(@PathVariable int dni) {
        EntidadDTO entidadDTO = entidadServicio.findByDni(dni)
                .orElse(null);
        return ResponseEntity.ok(entidadDTO);
    }

    @PostMapping
    public ResponseEntity<EntidadDTO> createEntidad(@Valid @RequestBody CreateEntidadDTO createEntidadDTO){
        EntidadDTO newEntidad = entidadServicio.createEntidad(createEntidadDTO);
        return new ResponseEntity<>(newEntidad, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        entidadServicio.deleteEntidad(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateEntidadDTO> updateEntidad(@PathVariable Long id, @Valid @RequestBody UpdateEntidadDTO updateEntidadDTO){
        EntidadDTO entidadDTO = entidadServicio.updateEntidad(id, updateEntidadDTO)
                .orElse(null);
        return ResponseEntity.ok(updateEntidadDTO);
    }

    @GetMapping("/filtrarYOrdenar")
    public ResponseEntity<List<EntidadDTO>> filtrarYOrdenar(
            @RequestParam(required = false) String rol_entidad,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        List<EntidadDTO> entidades = entidadServicio.filtrarYOrdenar(rol_entidad, sortBy, sortDir);
        return ResponseEntity.ok(entidades);
    }
}