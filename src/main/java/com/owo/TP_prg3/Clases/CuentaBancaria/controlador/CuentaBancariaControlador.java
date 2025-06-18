package com.owo.TP_prg3.Clases.CuentaBancaria.controlador;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CreateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.UpdateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.service.CuentaBancariaServicioImpl;
import com.owo.TP_prg3.Clases.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Clases.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cuentas-bancarias")
public class CuentaBancariaControlador {

    @Autowired
    private CuentaBancariaServicioImpl cuentaBancariaServicio;

    @GetMapping
    public ResponseEntity<List<CuentaBancariaDTO>> getAllCuentasBancarias(){
        List<CuentaBancariaDTO> cuentasBancarias = cuentaBancariaServicio.getAllCuentasBancarias();
        return ResponseEntity.ok(cuentasBancarias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaBancariaDTO> getCuentaBancariaById(@PathVariable Long id) {
        return cuentaBancariaServicio.getCuentaBancariaById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria con ID " + id + " no encontrada."));
    }

    @PostMapping
    public ResponseEntity<CuentaBancariaDTO> createCuentaBancaria(@Valid @RequestBody CreateCuentaBancariaDTO createCuentaBancariaDTO){
        CuentaBancariaDTO newCuenta = cuentaBancariaServicio.createCuentaBancaria(createCuentaBancariaDTO);
        return new ResponseEntity<>(newCuenta, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCuentaBancaria(@PathVariable Long id){
        cuentaBancariaServicio.deleteCuentaBancaria(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CuentaBancariaDTO> updateCuentaBancaria(@PathVariable Long id, @Valid @RequestBody UpdateCuentaBancariaDTO updateCuentaBancariaDTO){
        CuentaBancariaDTO updatedCuenta = cuentaBancariaServicio.updateCuentaBancaria(id, updateCuentaBancariaDTO)
                .orElse(null);
        return ResponseEntity.ok(updatedCuenta);
    }

    @GetMapping("/entidad/{entidadId}")
    public ResponseEntity<CuentaBancariaDTO> getCuentaBancariaByEntidadId(@PathVariable Long entidadId){
        return cuentaBancariaServicio.getCuentaBancariaByEntidadId(entidadId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria no encontrada para la entidad con ID " + entidadId + "."));
    }
}
