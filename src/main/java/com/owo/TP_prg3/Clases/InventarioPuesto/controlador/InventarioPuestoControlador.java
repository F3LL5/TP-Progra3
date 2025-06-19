package com.owo.TP_prg3.Clases.InventarioPuesto.controlador;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.service.InventarioPuestoServicioImpl;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    public List<Map<String,Object>> obtenerItemsEnStock(@PathVariable Long id) {
        List<Map<String,Object>> itemsEnStock=inventarioPuestoServicio.mostrarItemsEnStock(id);
        return itemsEnStock;
    }

    @GetMapping("/obtenerInvConStockBajo/{id}")
    public List<Map<String,Object>> obtenerItemsEnStockBajo(@PathVariable Long id) {
        List<Map<String,Object>> itemsEnStockBajo=inventarioPuestoServicio.mostrarItemsEnStockBajo(id);
        return itemsEnStockBajo;
    }

    @GetMapping("/filtrarYordenarItemsInventario")
    public List<Map<String,Object>> filtrarYordenarItemsInventario(
            @RequestParam Long id, @RequestParam(required = false)String categoria,
            @RequestParam(required = false)String orden, @RequestParam(required = false)String direccion){
        return inventarioPuestoServicio.filtrarYordenar(id,categoria,orden,direccion);
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
