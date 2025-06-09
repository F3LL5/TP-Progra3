package com.owo.TP_prg3.Clases.DetallePedido.controlador;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoControlador {

    @Autowired
    private DetallePedidoServicioImpl detallePedidoServicio;

    @GetMapping
    public ResponseEntity<List<DetallePedidoDTO>> getAllDetallesPedido(){
        List<DetallePedidoDTO> detallesPedido = detallePedidoServicio.getAllDetallesPedido();
        return ResponseEntity.ok(detallesPedido);
    }

    @GetMapping("/{id}")
    public DetallePedidoDTO getDetallePedidoById(@PathVariable Long id){
        return detallePedidoServicio.getDetallePedidoById(id).orElse(null);
    }

    @PostMapping
    public DetallePedidoDTO createDetallePedido(@Valid @RequestBody CreateDetallePedidoDTO createDetallePedidoDTO){
        return detallePedidoServicio.createDetallePedido(createDetallePedidoDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteDetallePedido(@PathVariable Long id){
        return detallePedidoServicio.deleteDetallePedido(id);
    }

    @PatchMapping("/{id}")
    public Optional<DetallePedidoDTO> updateDetallePedido(@PathVariable Long id, @Valid @RequestBody UpdateDetallePedidoDTO updateDetallePedidoDTO){
        return detallePedidoServicio.updateDetallePedido(id, updateDetallePedidoDTO);
    }
}
