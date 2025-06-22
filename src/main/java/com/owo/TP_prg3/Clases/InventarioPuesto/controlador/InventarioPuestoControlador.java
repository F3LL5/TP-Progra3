package com.owo.TP_prg3.Clases.InventarioPuesto.controlador;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.ItemStockDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.service.InventarioPuestoServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario-puesto")
public class InventarioPuestoControlador {

    @Autowired
    private InventarioPuestoServicioImpl inventarioPuestoServicio;

    // --- Métodos GET ---

    @GetMapping
    public ResponseEntity<List<InventarioPuestoDTO>> getAllInventarioPuestos(){
        List<InventarioPuestoDTO> inventarioPuestos = inventarioPuestoServicio.getAllInventarioPuestos();
        return ResponseEntity.ok(inventarioPuestos);
    }

    @GetMapping("/{id}")
    public InventarioPuestoDTO getInventarioPuestoById(@PathVariable Long id){
        return inventarioPuestoServicio.getInventarioPuestoById(id).orElse(null);
    }

    @GetMapping("/obtenerInvConStock/{id}")
    public ResponseEntity<List<ItemStockDTO>> obtenerItemsEnStock(@PathVariable Long id) {
        Optional<List<ItemStockDTO>> itemsEnStock=inventarioPuestoServicio.obtenerItemsEnStock(id);
        return ResponseEntity.ok(itemsEnStock.orElse(Collections.emptyList()));
    }

    @GetMapping("/obtenerInvConStockBajo/{id}")
    public ResponseEntity<List<ItemStockDTO>> obtenerItemsEnStockBajo(@PathVariable Long id) {
        Optional<List<ItemStockDTO>> itemsEnStockBajo=inventarioPuestoServicio.obtenerItemsEnStockBajo(id);
        return ResponseEntity.ok(itemsEnStockBajo.orElse(Collections.emptyList()));
    }

    @GetMapping("/filtrarYordenarItemsInventario")
    public ResponseEntity<List<InventarioPuestoDTO>> filtrarYordenarItemsInventario(@RequestParam Long idPuesto, @RequestParam(required = false)String categoriaItem,
                                                                                    @RequestParam(required = false)String sortBy, @RequestParam(required = false)String sortDir)
    {
        return ResponseEntity.ok(inventarioPuestoServicio.filtrarYordenar(idPuesto,categoriaItem,sortBy,sortDir));
    }

    @GetMapping("/puesto/{puestoId}")
    public ResponseEntity<List<InventarioPuestoDTO>> obtenerInventariosDeUnPuesto(@PathVariable Long puestoId) {
        List<InventarioPuestoDTO> inventarios = inventarioPuestoServicio.obtenerInventariosDeUnPuesto(puestoId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{id}/puesto/{puestoId}")
    public InventarioPuestoDTO getInventarioPuestoByIdAndPuestoId(@PathVariable Long id, @PathVariable Long puestoId) {
        return inventarioPuestoServicio.getInventarioPuestoById(id)
                .filter(inventario -> inventario.getPuestoId().equals(puestoId))
                .orElse(null);
    }

    // --- Métodos POST ---

    @PostMapping
    public InventarioPuestoDTO createInventarioPuesto(@Valid @RequestBody CreateInventarioPuestoDTO createInventarioPuestoDTO){
        return inventarioPuestoServicio.createInventarioPuesto(createInventarioPuestoDTO);
    }

    // --- Métodos DELETE ---

    @DeleteMapping("/{id}")
    public boolean deleteInventarioPuesto(@PathVariable Long id){
        return inventarioPuestoServicio.deleteInventarioPuesto(id);
    }

    @DeleteMapping("/{id}/puesto/{puestoId}")
    public boolean deleteInventarioPuesto(@PathVariable Long id, @PathVariable Long puestoId){
        return inventarioPuestoServicio.deleteInventarioPuesto(id, puestoId);
    }

    // --- Métodos PATCH ---

    @PatchMapping("/{id}")
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(@PathVariable Long id, @Valid @RequestBody UpdateInventarioPuestoDTO updateInventarioPuestoDTO){
        return inventarioPuestoServicio.updateInventarioPuesto(id, updateInventarioPuestoDTO);
    }

    @PatchMapping("/{id}/puesto/{puestoId}")
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(@PathVariable Long id, @PathVariable Long puestoId, @Valid @RequestBody UpdateInventarioPuestoDTO updateInventarioPuestoDTO){ // Modificado: Recibe puestoId
        return inventarioPuestoServicio.updateInventarioPuesto(id, puestoId, updateInventarioPuestoDTO);
    }
}
