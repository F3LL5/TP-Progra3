package com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.controlador;

import com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.dto.InventarioCostoHistorialDTO;
import com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.service.InventarioCostoHistorialServiciolmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/InventarioCostoHistorial")
public class InventarioCostoHistorialControlador {
    @Autowired
    private InventarioCostoHistorialServiciolmpl inventarioCostoHistorialServiciolmpl;

    @GetMapping
    public ResponseEntity <List<InventarioCostoHistorialDTO>> getAll(){
        List<InventarioCostoHistorialDTO> inventarioCostoHistorialDTOS = inventarioCostoHistorialServiciolmpl.getAll();
        return ResponseEntity.ok(inventarioCostoHistorialDTOS);
    }

    @GetMapping("/{id}")
    public InventarioCostoHistorialDTO buscarPorID(@PathVariable Long id){
        return inventarioCostoHistorialServiciolmpl.buscarPorID(id).orElse(null);
    }

    @GetMapping("/listado")
    public String obtenerTodosString(){return inventarioCostoHistorialServiciolmpl.listado();}

}
