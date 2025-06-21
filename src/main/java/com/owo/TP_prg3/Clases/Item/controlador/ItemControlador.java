package com.owo.TP_prg3.Clases.Item.controlador;

import com.owo.TP_prg3.Clases.Item.dto.CreateItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;
import com.owo.TP_prg3.Clases.Item.modelo.Item;
import com.owo.TP_prg3.Clases.Item.service.ItemServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemControlador {

    @Autowired
    private ItemServicioImpl itemServicio;

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems(){
        List<ItemDTO> items = itemServicio.getAllProducts();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        ItemDTO item = itemServicio.getProductById(id)
                .orElse(null);
        return ResponseEntity.ok(item);
    }


    @GetMapping("/listado")
    public ResponseEntity<String> obtenerTodosString() {
        return ResponseEntity.ok(itemServicio.listado());
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody CreateItemDTO createItemDTO) {
        ItemDTO newItem = itemServicio.createProduct(createItemDTO);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED); // 201 Created
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemServicio.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody UpdateItemDTO updateItemDTO) {
        ItemDTO updatedItem = itemServicio.updateProduct(id, updateItemDTO)
                .orElse(null);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/filtrarYordenar")
    public ResponseEntity<List<ItemDTO>> filtrarYordenar(
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String direccion
    ) {
        List<ItemDTO> items = itemServicio.filtrarYordenar(puestoId, categoria, orden, direccion);
        return ResponseEntity.ok(items);
    }


}