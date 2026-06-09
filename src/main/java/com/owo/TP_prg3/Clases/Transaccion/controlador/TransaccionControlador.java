package com.owo.TP_prg3.Clases.Transaccion.controlador;

import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Transaccion.dto.FormTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.service.TransaccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionControlador implements I_Controlador<TransaccionDTO, FormTransaccionDTO> {

    @Autowired
    private TransaccionServicio transaccionServicio;

    // --- Métodos GET (Lectura) ---
    @Override
    @GetMapping
    public ResponseEntity<Set<TransaccionDTO>> obtenerTodos() {
        return ResponseEntity.ok(transaccionServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<TransaccionDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(transaccionServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<TransaccionDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(transaccionServicio.ordenar(campo, ascendente));
    }

     @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=proveedores.xlsx");
        return ResponseEntity.ok().headers(headers).body(transaccionServicio.exportar());
    }

    // --- Métodos POST (Creación) ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormTransaccionDTO dto) {
        boolean exito = transaccionServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true);
        else return ResponseEntity.badRequest().body(false);
    }

    @PostMapping("/movimiento")
    public ResponseEntity<TransaccionDTO> registrarMovimiento(@RequestBody FormTransaccionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionServicio.registrarMovimiento(dto));
    }

    // --- Métodos PUT (Actualización) ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormTransaccionDTO dto) {
        return ResponseEntity.ok(transaccionServicio.actualizar(id, dto));
    }

    // --- Métodos DELETE (Eliminación) ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(transaccionServicio.eliminar(id));
    }
}
