package com.owo.TP_prg3.Clases.ItemPrecioHistorial.controlador;

import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.CreateItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.ItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.dto.UpdateItemPrecioHistorialDTO;
import com.owo.TP_prg3.Clases.ItemPrecioHistorial.service.ItemPrecioHistorialServiciolmpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ItemPrecioHistorial")
public class ItemPrecioHistorialControlador {
    @Autowired
    private ItemPrecioHistorialServiciolmpl itemPrecioHistorialServiciolmpl;

    @GetMapping
    public ResponseEntity <List<ItemPrecioHistorialDTO>> getAllItemsHistorial(){
        List<ItemPrecioHistorialDTO> itemPrecioHistorialDTOS=itemPrecioHistorialServiciolmpl.getAllProducts();
        return ResponseEntity.ok(itemPrecioHistorialDTOS);
    }

    @GetMapping("/{id}")
    public ItemPrecioHistorialDTO getItemHistorialById(@PathVariable Long id){
        return itemPrecioHistorialServiciolmpl.getProductById(id).orElse(null);
    }

    @GetMapping("/listado")
    public String obtenerTodosString(){return itemPrecioHistorialServiciolmpl.listado();}

    @PostMapping
    public ItemPrecioHistorialDTO createItemHistorial(@Valid @RequestBody CreateItemPrecioHistorialDTO createItemPrecioHistorialDTO){
    return  itemPrecioHistorialServiciolmpl.crateProduct(createItemPrecioHistorialDTO) ;
    }

    @DeleteMapping ("/{id}")
    public boolean deleteItem(@PathVariable Long id) {
        return itemPrecioHistorialServiciolmpl.deleteProduct(id);
    }

    @PatchMapping("/{id}")
    public Optional<ItemPrecioHistorialDTO> updateItemHistorial(@PathVariable Long id, @Valid @RequestBody UpdateItemPrecioHistorialDTO updateItemPrecioHistorialDTO){
        return itemPrecioHistorialServiciolmpl.updateProduct(id,updateItemPrecioHistorialDTO);
    }

}
