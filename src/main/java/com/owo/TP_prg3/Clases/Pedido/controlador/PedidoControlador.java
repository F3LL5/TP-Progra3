package com.owo.TP_prg3.Clases.Pedido.controlador;

import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.service.PedidoServicioImpl;
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
}
