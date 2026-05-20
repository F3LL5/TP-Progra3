package com.owo.TP_prg3.Clases.Lote.controlador;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Lote.dto.FormLoteDTO;
import com.owo.TP_prg3.Clases.Lote.dto.LoteDTO;
import com.owo.TP_prg3.Clases.Lote.service.LoteServicio;

@RestController
@RequestMapping("api/lotes")
public class LoteControlador implements I_Controlador<LoteDTO, FormLoteDTO>{

    @Autowired
    private LoteServicio loteServicio;

    @Override
    @GetMapping
    public ResponseEntity<Set<LoteDTO>> obtenerTodos() {
        return ResponseEntity.ok(loteServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<LoteDTO> buscarPorID(@PathVariable Long id) { 
        return ResponseEntity.ok(loteServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<LoteDTO>> filtrar(String campo, Object valor) {
        return ResponseEntity.ok(loteServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<LoteDTO>> ordenar(String campo, boolean ascendente) {
        return ResponseEntity.ok(loteServicio.ordenar(campo, ascendente));
    }

     @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=proveedores.xlsx");
        return ResponseEntity.ok().headers(headers).body(loteServicio.exportar());
    }

    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormLoteDTO dto) { 
        boolean exito = loteServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true);
        else return ResponseEntity.badRequest().body(false);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormLoteDTO dto) { 
        return ResponseEntity.ok(loteServicio.actualizar(id, dto));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(loteServicio.eliminar(id));
    }

}