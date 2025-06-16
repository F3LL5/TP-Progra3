package com.owo.TP_prg3.Auditoria.DuenioHistorial.controlador;

import com.owo.TP_prg3.Auditoria.DuenioHistorial.dto.DuenioHistorialDTO;
import com.owo.TP_prg3.Auditoria.DuenioHistorial.service.DuenioHistorialServiciolmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/DuenioHistorial")
public class DuenioHistorialControlador {
    @Autowired
    private DuenioHistorialServiciolmpl duenioHistorialServiciolmpl;

    @GetMapping
    public ResponseEntity <List<DuenioHistorialDTO>> getAll(){
        List<DuenioHistorialDTO> duenioHistorialDTOS=duenioHistorialServiciolmpl.getAll();
        return ResponseEntity.ok(duenioHistorialDTOS);
    }

    @GetMapping ("/{id}")
    public DuenioHistorialDTO buscarPorId(@PathVariable Long id){
        return duenioHistorialServiciolmpl.buscarPorId(id).orElse(null);
    }

    @GetMapping("/listado")
    public String obtenerTodos(){
        return duenioHistorialServiciolmpl.listado();
    }


}
