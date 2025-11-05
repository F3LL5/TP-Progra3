package com.owo.TP_prg3.Clases.DetallePedido.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.FormDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicio;

import java.util.Set;

@RestController
@RequestMapping("/api/detallespedido")
public class DetallePedidoControlador implements I_Controlador<DetallePedidoDTO, FormDetallePedidoDTO> {

    @Autowired
    private DetallePedidoServicio detallePedidoServicio;

    // --- Métodos GET ---
    @Override
    @GetMapping
    public ResponseEntity<Set<DetallePedidoDTO>> obtenerTodos(){
        return ResponseEntity.ok(detallePedidoServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoDTO> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(detallePedidoServicio.buscarPorID(id).orElse(null));
    }
    
    // Nuevo endpoint: Buscar detalles por ID de Pedido
    @GetMapping("/porpedido/{pedidoId}")
    public ResponseEntity<Set<DetallePedidoDTO>> buscarPorPedidoId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(detallePedidoServicio.buscarPorPedidoId(pedidoId));
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<DetallePedidoDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(detallePedidoServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<DetallePedidoDTO>> ordenar(@RequestParam String campo, @RequestParam boolean ascendente) {
        return ResponseEntity.ok(detallePedidoServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormDetallePedidoDTO dto) {
        boolean exito = detallePedidoServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true); 
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormDetallePedidoDTO dto) {
        return ResponseEntity.ok(detallePedidoServicio.actualizar(id, dto)); 
    }

    // --- Métodos DELETE ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(detallePedidoServicio.eliminar(id));
    }
}
