package com.owo.TP_prg3.Clases.Producto.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Producto.dto.FormProductoDTO;
import com.owo.TP_prg3.Clases.Producto.dto.ProductoDTO;
import com.owo.TP_prg3.Clases.Producto.service.ProductoServicio;
import java.util.Set;

@RestController
@RequestMapping("/api/productos")
public class ProductoControlador implements I_Controlador<ProductoDTO, FormProductoDTO> {

    @Autowired
    private ProductoServicio productoServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<ProductoDTO>> obtenerTodos(){
        Set<ProductoDTO> productos = productoServicio.obtenerTodos();
        return ResponseEntity.ok(productos);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(productoServicio.buscarPorID(id).orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<ProductoDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(productoServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<ProductoDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(productoServicio.ordenar(campo, ascendente));
    }

     @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=proveedores.xlsx");
        return ResponseEntity.ok().headers(headers).body(productoServicio.exportar());
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormProductoDTO dto) {
        boolean exito = productoServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormProductoDTO dto) {
        return ResponseEntity.ok(productoServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(productoServicio.eliminar(id)); 
    }
    
}