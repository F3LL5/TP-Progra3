package com.owo.TP_prg3.Clases.InventarioPuesto.controlador;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.service.InventarioPuestoServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario-puesto")
public class InventarioPuestoControlador {

    @Autowired
    private InventarioPuestoServicioImpl inventarioPuestoServicio;

    @GetMapping
    public ResponseEntity<List<InventarioPuestoDTO>> getAllInventarioPuestos(){
        List<InventarioPuestoDTO> inventarioPuestos = inventarioPuestoServicio.getAllInventarioPuestos();
        return ResponseEntity.ok(inventarioPuestos);
    }

    @GetMapping("/{id}")
    public InventarioPuestoDTO getInventarioPuestoById(@PathVariable Long id){
        return inventarioPuestoServicio.getInventarioPuestoById(id).orElse(null);
    }

    @PostMapping
    public InventarioPuestoDTO createInventarioPuesto(@Valid @RequestBody CreateInventarioPuestoDTO createInventarioPuestoDTO){
        return inventarioPuestoServicio.createInventarioPuesto(createInventarioPuestoDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteInventarioPuesto(@PathVariable Long id){
        return inventarioPuestoServicio.deleteInventarioPuesto(id);
    }

    @PatchMapping("/{id}")
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(@PathVariable Long id, @Valid @RequestBody UpdateInventarioPuestoDTO updateInventarioPuestoDTO){
        return inventarioPuestoServicio.updateInventarioPuesto(id, updateInventarioPuestoDTO);
    }
}
