package com.owo.TP_prg3.Clases.Pedido.controlador;

import com.owo.TP_prg3.Clases.Item.dto.ItemDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO2;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicio;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicioImpl;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<PedidoDTO>getPedidoById(@PathVariable Long id){
        PedidoDTO pedido=pedidoServicio.getPedidoById(id)
                .orElse(null);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> createPedido(@Valid @RequestBody CreatePedidoDTO createPedidoDTO) {
        PedidoDTO nuevoPedido = pedidoServicio.createPedido(createPedidoDTO);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @PostMapping("/createPedidoYtransaccion")
    public ResponseEntity<PedidoDTO> createPedidoYtransaccion(@Valid @RequestBody CreatePedidoDTO2 createPedidoDTO2) {
        PedidoDTO nuevoPedido = pedidoServicio.createPedidoYtransaccion(createPedidoDTO2);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoServicio.deletePedido(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}")
    public ResponseEntity<PedidoDTO> updatePedido(@PathVariable Long id, @Valid @RequestBody UpdatePedidoDTO updatePedidoDTO) {
        Optional<PedidoDTO> updatedPedido = pedidoServicio.updatePedido(id, updatePedidoDTO);

        return updatedPedido
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/filtrarYordenar")
    public ResponseEntity<List<PedidoDTO>> filtrarYordenarPedidos(
            @RequestParam(required = false) String tipoTransaccion,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String direccion
    ) {
        List<PedidoDTO> pedidos = pedidoServicio.filtrarYOrdenar(tipoTransaccion, orden, direccion);
        return ResponseEntity.ok(pedidos);
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
