package com.owo.TP_prg3.Clases.Transaccion.controlador;

import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.Transaccion.dto.CreateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.UpdateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.service.TransaccionServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionControlador {

    @Autowired
    private TransaccionServicioImpl transaccionServicio;

    @GetMapping
    public ResponseEntity<List<TransaccionDTO>> getAllTransacciones(){
        List<TransaccionDTO> transacciones = transaccionServicio.getAllTransacciones();
        return ResponseEntity.ok(transacciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> getCuentaBancariaById(@PathVariable Long id) {
        return transaccionServicio.getTransaccionById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción con ID " + id + " no encontrada."));
    }

    @PostMapping
    public ResponseEntity<TransaccionDTO> createTransaccion(@Valid @RequestBody CreateTransaccionDTO createTransaccionDTO){
        TransaccionDTO transaccionDTO = transaccionServicio.createTransaccion(createTransaccionDTO);
        return new ResponseEntity<>(transaccionDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCuentaBancaria(@PathVariable Long id){
        transaccionServicio.deleteTransaccion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransaccionDTO> updateTransaccion(@PathVariable Long id, @Valid @RequestBody UpdateTransaccionDTO updateTransaccionDTO) {
        TransaccionDTO updated = transaccionServicio.updateTransaccion(id, updateTransaccionDTO)
                .orElse(null);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filtrarYOrdenar")
    public ResponseEntity<List<TransaccionDTO>> filtrarYOrdenar(
            @RequestParam(required = false) String tipo_transaccion,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir

    ) {
        List<TransaccionDTO> transaccion = transaccionServicio.filtrarYOrdenar(tipo_transaccion, sortBy, sortDir);
        return ResponseEntity.ok(transaccion);
    }


    @GetMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<TransaccionDTO> getTransaccionByIdAndPuestoId(@PathVariable Long id, @PathVariable Long puestoId) {
        TransaccionDTO transaccionDTO = transaccionServicio.getTransaccionByIdAndPuestoId(id, puestoId)
                .orElse(null);
        return ResponseEntity.ok(transaccionDTO);
    }

    @GetMapping("/puesto/{puestoId}/filtrarYOrdenar")
    public ResponseEntity<List<TransaccionDTO>> filtrarYOrdenarTransaccionesByPuestoId(
            @PathVariable Long puestoId,
            @RequestParam(required = false) String tipo_transaccion,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        List<TransaccionDTO> transacciones = transaccionServicio.filtrarYOrdenarTransaccionesByPuestoId(
                puestoId, tipo_transaccion, sortBy, sortDir
        );
        return ResponseEntity.ok(transacciones);
    }

    @GetMapping("/puesto/{puestoId}")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesByPuestoId(@PathVariable Long puestoId) {
        List<TransaccionDTO> transacciones = transaccionServicio.getTransaccionesByPuestoId(puestoId);
        return ResponseEntity.ok(transacciones);
    }

























}
