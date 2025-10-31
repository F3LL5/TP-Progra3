package com.owo.TP_prg3.Clases.Producto.controlador;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Producto.dto.FormProductoDTO;
import com.owo.TP_prg3.Clases.Producto.dto.ProductoDTO;
import com.owo.TP_prg3.Clases.Producto.service.ProductoServicio;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/productos")
public class ProductoControlador implements I_Controlador<ProductoDTO, FormProductoDTO, Long> {

    @Autowired
    private ProductoServicio productoServicio;

    @Override
    @GetMapping
    public ResponseEntity<Set<ProductoDTO>> obtenerTodos(){
        return ResponseEntity.ok(productoServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorID(@PathVariable Long id) {
        Optional<ProductoDTO> optional = productoServicio.buscarPorID(id);
        return ResponseEntity.ok(optional.orElse(null));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<ProductoDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        Set<ProductoDTO> resultados = productoServicio.filtrar(campo, valor);
        return ResponseEntity.ok(resultados);
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<ProductoDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        Set<ProductoDTO> resultados = productoServicio.ordenar(campo, ascendente);
        return ResponseEntity.ok(resultados);
    }

    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormProductoDTO dto) {
        boolean exito = productoServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormProductoDTO dto) {
        Boolean exito = Boolean.valueOf(productoServicio.actualizar(id, dto));
        return ResponseEntity.ok(exito); 
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        boolean exito = productoServicio.eliminar(id);
        return ResponseEntity.ok(exito); 
    }
    
}