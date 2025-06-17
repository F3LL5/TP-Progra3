package com.owo.TP_prg3.Clases.Pedido.controlador;

import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicioImpl;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador {

    @Autowired
    private PedidoServicioImpl pedidoServicio;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAllPedidos(){
        List<PedidoDTO> pedidos = pedidoServicio.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public PedidoDTO getPedidoById(@PathVariable Long id){
        return pedidoServicio.getPedidoById(id).orElse(null);
    }

    @PostMapping
    public PedidoDTO createPedido(@Valid @RequestBody CreatePedidoDTO createPedidoDTO){
        return pedidoServicio.createPedido(createPedidoDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deletePedido(@PathVariable Long id){
        return pedidoServicio.deletePedido(id);
    }

    @PatchMapping("/{id}")
    public Optional<PedidoDTO> updatePedido(@PathVariable Long id, @Valid @RequestBody UpdatePedidoDTO updatePedidoDTO){
        return pedidoServicio.updatePedido(id, updatePedidoDTO);
    }

    @GetMapping("/filtrarYOrdenar")
    public List<PedidoDTO> filtrarYOrdenar(
            @RequestParam(required = false) String tipo_transaccion,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir

    ) {
        return pedidoServicio.filtrarYOrdenar(tipo_transaccion, sortBy, sortDir);
    }

    // Obtener todos los pedidos asociados a un puesto específico
    @GetMapping("/puesto/{puestoId}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByPuestoId(@PathVariable Long puestoId) {
        List<PedidoDTO> pedidos = pedidoServicio.getPedidosByPuestoId(puestoId);
        return ResponseEntity.ok(pedidos);
    }

    // Buscar pedido por ID y Puesto ID
    @GetMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<PedidoDTO> getPedidoByIdAndPuestoId(@PathVariable Long id, @PathVariable Long puestoId) {
        PedidoDTO pedidoDTO = pedidoServicio.getPedidoByIdAndPuestoId(id, puestoId)
                .orElse(null);
        return ResponseEntity.ok(pedidoDTO);
    }

    // Eliminar Pedido de un Puesto específico
    @DeleteMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<Boolean> deletePedidoFromPuesto(@PathVariable Long id, @PathVariable Long puestoId) {
        boolean deleted = pedidoServicio.deletePedidoFromPuesto(id, puestoId);
        return ResponseEntity.ok(deleted);
    }

    // Modificar Pedido de un Puesto específico
    @PatchMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<PedidoDTO> updatePedidoForPuesto(@PathVariable Long id, @PathVariable Long puestoId, @Valid @RequestBody UpdatePedidoDTO updatePedidoDTO){
        PedidoDTO pedidoDTO = pedidoServicio.updatePedidoForPuesto(id, puestoId, updatePedidoDTO)
                .orElse(null);
        return ResponseEntity.ok(pedidoDTO);
    }
}
