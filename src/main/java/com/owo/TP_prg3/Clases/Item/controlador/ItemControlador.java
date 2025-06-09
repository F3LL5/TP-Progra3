package com.owo.TP_prg3.Clases.Item.controlador;

import com.owo.TP_prg3.Clases.Item.dto.CreateItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Item.dto.UpdateItemDTO;
import com.owo.TP_prg3.Clases.Item.service.ItemServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ItemDTO getItemById(@PathVariable Long id){
        return itemServicio.getProductById(id).get();
    }

    @PostMapping
    public ItemDTO createItem(@RequestBody CreateItemDTO createItemDTO){
        return itemServicio.createProduct(createItemDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteItem(@PathVariable Long id){
        return itemServicio.deleteProduct(id);
    }

    @PatchMapping("/{id}")
    public Optional<ItemDTO> updateItem(@PathVariable Long id, @RequestBody UpdateItemDTO updateItemDTO){
        return itemServicio.updateProduct(id, updateItemDTO);
    }
}