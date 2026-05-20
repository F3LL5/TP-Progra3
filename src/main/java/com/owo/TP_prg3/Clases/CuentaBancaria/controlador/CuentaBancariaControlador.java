package com.owo.TP_prg3.Clases.CuentaBancaria.controlador;


import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.FormCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.service.CuentaBancariaServicio;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;

@RestController
@RequestMapping("/api/cuenta_bancarias")
public class CuentaBancariaControlador implements I_Controlador<CuentaBancariaDTO, FormCuentaBancariaDTO> {

    @Autowired
    private CuentaBancariaServicio cuentabancariaServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<CuentaBancariaDTO>> obtenerTodos(){
        return ResponseEntity.ok(cuentabancariaServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CuentaBancariaDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(cuentabancariaServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<CuentaBancariaDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(cuentabancariaServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<CuentaBancariaDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(cuentabancariaServicio.ordenar(campo, ascendente));
    }

      @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=proveedores.xlsx");
        return ResponseEntity.ok().headers(headers).body(cuentabancariaServicio.exportar());
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormCuentaBancariaDTO dto) {
        boolean exito = cuentabancariaServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormCuentaBancariaDTO dto) {
        return ResponseEntity.ok(cuentabancariaServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(cuentabancariaServicio.eliminar(id)); 
    }
}
