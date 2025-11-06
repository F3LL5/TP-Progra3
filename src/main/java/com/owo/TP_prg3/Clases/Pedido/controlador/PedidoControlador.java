package com.owo.TP_prg3.Clases.Pedido.controlador;

import com.owo.TP_prg3.Clases.Interfaces.I_Controlador;
import com.owo.TP_prg3.Clases.Pedido.dto.FormPedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador implements I_Controlador<PedidoDTO, FormPedidoDTO> {

    @Autowired
    private PedidoServicio pedidoServicio;

    // --- Métodos GET (Lectura) ---
    @Override
    @GetMapping
    public ResponseEntity<Set<PedidoDTO>> obtenerTodos() {
        return ResponseEntity.ok(pedidoServicio.obtenerTodos());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorID(@PathVariable Long id) {
        PedidoDTO dto = pedidoServicio.buscarPorID(id).orElse(null);
        if (dto != null) return ResponseEntity.ok(dto);
        return ResponseEntity.notFound().build();
    }

    @Override
    @GetMapping("/filtrar")
    public ResponseEntity<Set<PedidoDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(pedidoServicio.filtrar(campo, valor));
    }

    @Override
    @GetMapping("/ordenar")
    public ResponseEntity<Set<PedidoDTO>> ordenar(@RequestParam String campo, @RequestParam(defaultValue = "true") boolean ascendente) {
        return ResponseEntity.ok(pedidoServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST (Creación) ---
    @Override
    @PostMapping
    public ResponseEntity<Boolean> cargar(@RequestBody FormPedidoDTO dto) {
        boolean exito = pedidoServicio.cargar(dto);
        if (exito) return ResponseEntity.status(HttpStatus.CREATED).body(true);
        else return ResponseEntity.badRequest().body(false);
    }

    // --- Métodos PUT (Actualización) ---
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormPedidoDTO dto) {
        boolean exito = pedidoServicio.actualizar(id, dto);
        if (exito) return ResponseEntity.ok(true);
        else return ResponseEntity.notFound().build();
    }

    // --- Métodos DELETE (Eliminación) ---
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        boolean exito = pedidoServicio.eliminar(id);
        if (exito) return ResponseEntity.ok(true);
        else return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Boolean> finalizarPedido(@PathVariable Long id) {
        try {
            boolean exito = pedidoServicio.finalizarPedido(id);
            if (exito) return ResponseEntity.ok(true);
            else return ResponseEntity.badRequest().body(false);
        } catch (Exception e) {
            System.err.println("Error al finalizar pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
