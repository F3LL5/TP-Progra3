package com.owo.TP_prg3.Clases.CuentaBancaria.controlador;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CreateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.UpdateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.service.CuentaBancariaServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CuentaBancariaDTO getCuentaBancariaById(@PathVariable Long id){
        return cuentaBancariaServicio.getCuentaBancariaById(id).orElse(null);
    }

    @GetMapping("/listado")
    public String obtenerTodosString(){
        return cuentaBancariaServicio.listado();
    }

    @PostMapping
    public ResponseEntity<String> createCuentaBancaria(@Valid @RequestBody CreateCuentaBancariaDTO createCuentaBancariaDTO){
        try {
            cuentaBancariaServicio.createCuentaBancaria(createCuentaBancariaDTO);
            return ResponseEntity.ok("Cuenta bancaria creada exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public boolean deleteCuentaBancaria(@PathVariable Long id){
        return cuentaBancariaServicio.deleteCuentaBancaria(id);
    }

    @PatchMapping("/{id}")
    public Optional<CuentaBancariaDTO> updateCuentaBancaria(@PathVariable Long id, @Valid @RequestBody UpdateCuentaBancariaDTO updateCuentaBancariaDTO){
        return cuentaBancariaServicio.updateCuentaBancaria(id, updateCuentaBancariaDTO);
    }
}
