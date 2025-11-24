package com.owo.TP_prg3.Clases.Pedido.controlador;


import com.owo.TP_prg3.Clases.Pedido.dto.FormPedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador  {

    @Autowired
    private PedidoServicio pedidoServicio;

    // --- Métodos GET (Lectura) ---
    @GetMapping
    public ResponseEntity<Set<PedidoDTO>> obtenerTodos() {
        return ResponseEntity.ok(pedidoServicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorID(@PathVariable Long id) {
        PedidoDTO dto = pedidoServicio.buscarPorID(id).orElse(null);
        if (dto != null) return ResponseEntity.ok(dto);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Set<PedidoDTO>> filtrar(@RequestParam String campo, @RequestParam Object valor) {
        return ResponseEntity.ok(pedidoServicio.filtrar(campo, valor));
    }

    @GetMapping("/ordenar")
    public ResponseEntity<Set<PedidoDTO>> ordenar(@RequestParam String campo, @RequestParam(defaultValue = "true") boolean ascendente) {
        return ResponseEntity.ok(pedidoServicio.ordenar(campo, ascendente));
    }

    // --- Métodos POST (Creación) ---
    @PostMapping
    public ResponseEntity<Pedido> cargar(@RequestBody FormPedidoDTO dto) {
        Pedido pedido = pedidoServicio.cargar(dto); 
        if (pedido != null) return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        else return ResponseEntity.badRequest().body(null); 
    }

    // --- Métodos PUT (Actualización) ---
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody FormPedidoDTO dto) {
        boolean exito = pedidoServicio.actualizar(id, dto);
        if (exito) return ResponseEntity.ok(true);
        else return ResponseEntity.notFound().build();
    }

    // --- Métodos DELETE (Eliminación) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        boolean exito = pedidoServicio.eliminar(id);
        if (exito) return ResponseEntity.ok(true);
        else return ResponseEntity.notFound().build();
    }

  @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarPedido(@PathVariable Long id) {
    try {
        boolean exito = pedidoServicio.finalizarPedido(id);

        if (exito) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", "No se pudo finalizar el pedido."));
        }

    } catch (Exception e) {
        System.err.println("Error al finalizar pedido: " + e.getMessage());

        String msg = (e.getMessage() != null && !e.getMessage().isBlank())
                ? e.getMessage()
                : "Error interno al finalizar el pedido.";

        // mando SIEMPRE 400 con el mensaje real para que Angular lo muestre
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", msg));
    }
    }
}
