package com.owo.TP_prg3.Clases.Puesto.controlador;

import com.owo.TP_prg3.Clases.Puesto.dto.CreatePuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.dto.UpdatePuestoDTO;
import com.owo.TP_prg3.Clases.Puesto.service.PuestoServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/puestos")
public class PuestoControlador {

    @Autowired
    private PuestoServicioImpl puestoServicio;

    @GetMapping
    public ResponseEntity<List<PuestoDTO>> getAllPuestos(){
        List<PuestoDTO> puestos = puestoServicio.getAllPuestos();
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/{id}")
    public PuestoDTO getPuestoById(@PathVariable Long id){
        return puestoServicio.getPuestoById(id).orElse(null);
    }

    @PostMapping
    public PuestoDTO createPuesto(@Valid @RequestBody CreatePuestoDTO createPuestoDTO){
        return puestoServicio.createPuesto(createPuestoDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deletePuesto(@PathVariable Long id){
        return puestoServicio.deletePuesto(id);
    }

    @PatchMapping("/{id}")
    public Optional<PuestoDTO> updatePuesto(@PathVariable Long id, @Valid @RequestBody UpdatePuestoDTO updatePuestoDTO){
        return puestoServicio.updatePuesto(id, updatePuestoDTO);
    }
}
