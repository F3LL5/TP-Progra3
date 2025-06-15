package com.owo.TP_prg3.Auditoria.ItemCostoHistorial.controlador;

import com.owo.TP_prg3.Auditoria.ItemCostoHistorial.dto.ItemCostoHistorialDTO;
import com.owo.TP_prg3.Auditoria.ItemCostoHistorial.service.ItemCostoHistorialServiciolmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ItemCostoHistorial")
public class ItemCostoHistorialControlador {
    @Autowired
    private ItemCostoHistorialServiciolmpl itemCostoHistorialServiciolmpl;

    @GetMapping
    public ResponseEntity <List<ItemCostoHistorialDTO>> getAll(){
        List<ItemCostoHistorialDTO> itemCostoHistorialDTOS = itemCostoHistorialServiciolmpl.getAll();
        return ResponseEntity.ok(itemCostoHistorialDTOS);
    }

    @GetMapping("/{id}")
    public ItemCostoHistorialDTO buscarPorID(@PathVariable Long id){
        return itemCostoHistorialServiciolmpl.buscarPorID(id).orElse(null);
    }

    @GetMapping("/listado")
    public String obtenerTodosString(){return itemCostoHistorialServiciolmpl.listado();}

}
