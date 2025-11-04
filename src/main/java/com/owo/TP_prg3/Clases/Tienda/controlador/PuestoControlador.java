package com.owo.TP_prg3.Clases.Tienda.controlador;

import com.owo.TP_prg3.Clases.Tienda.dto.CreatePuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.PuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.UpdatePuestoDTO;
import com.owo.TP_prg3.Clases.Tienda.service.PuestoServicioImpl;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puestos")
public class PuestoControlador {

    @Autowired
    private PuestoServicioImpl puestoServicio;

    @GetMapping
    public ResponseEntity<List<PuestoDTO>> getAllPuestos() {
        List<PuestoDTO> puestos = puestoServicio.getAllPuestos();
        return ResponseEntity.ok(puestos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuestoDTO> getPuestoById(@PathVariable Long id) {
        return puestoServicio.getPuestoById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto con ID " + id + " no encontrado."));
    }

    @PostMapping
    public ResponseEntity<PuestoDTO> createPuesto(@Valid @RequestBody CreatePuestoDTO createPuestoDTO) {
        PuestoDTO newPuesto = puestoServicio.createPuesto(createPuestoDTO);
        return new ResponseEntity<>(newPuesto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePuesto(@PathVariable Long id) {
        puestoServicio.deletePuesto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PuestoDTO> updatePuesto(@PathVariable Long id, @Valid @RequestBody UpdatePuestoDTO updatePuestoDTO) {
        PuestoDTO updatedPuesto = puestoServicio.updatePuesto(id, updatePuestoDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto con ID " + id + " no encontrado."));
        return ResponseEntity.ok(updatedPuesto);
    }
    @PatchMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<PuestoDTO> updateMiPuesto(@PathVariable Long id, @Valid @RequestBody UpdatePuestoDTO updatePuestoDTO,@PathVariable Long puestoId) {
        PuestoDTO updatedPuesto = puestoServicio.updateMiPuesto(id, updatePuestoDTO, puestoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Puesto con ID " + id + " no encontrado."));
        return ResponseEntity.ok(updatedPuesto);
    }

    @GetMapping("/filtrarYOrdenarPorNombre")
    public ResponseEntity<List<PuestoDTO>> ordenarPorNombre(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        List<PuestoDTO> puestosFiltrados = puestoServicio.filtrarYOrdenarPorNombre(sortBy,sortDir);
        return ResponseEntity.ok(puestosFiltrados);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<PuestoDTO> getPuestoByDni(@PathVariable int dni) {
        return puestoServicio.getPuestoByDni(dni)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró un puesto para el DNI " + dni + "."));
    }
}
