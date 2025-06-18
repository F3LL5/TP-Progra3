package com.owo.TP_prg3.Clases.DetallePedido.controlador;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicioImpl;
import com.owo.TP_prg3.Clases.Excepciones.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @DeleteMapping("/{id}/puesto/{puestoId}")
    public Optional<DetallePedidoDTO> deleteDetallePedidoForPuesto(@PathVariable Long id, @PathVariable Long puestoId){
        // Primero, verifica si el detalle de pedido pertenece al puesto
        Optional<DetallePedidoDTO> detalle = detallePedidoServicio.getDetallePedidoByIdAndPuestoId(id, puestoId);
        if (detalle.isPresent()) {
            if(detallePedidoServicio.deleteDetallePedido(id))
            {
                return detalle;
            };
        }
        throw new RecursoNoEncontradoException("DetallePedido con ID " + id + " no encontrado para eliminar.");
    }

    @PatchMapping("/{id}/puesto/{puestoId}")
    public Optional<DetallePedidoDTO> updateDetallePedidoForPuesto(@PathVariable Long id, @PathVariable Long puestoId, @Valid @RequestBody UpdateDetallePedidoDTO updateDetallePedidoDTO){
        // Primero, verifica si el detalle de pedido pertenece al puesto
        Optional<DetallePedidoDTO> detalle = detallePedidoServicio.getDetallePedidoByIdAndPuestoId(id, puestoId);
        if (detalle.isPresent()) {
            return detallePedidoServicio.updateDetallePedido(id, updateDetallePedidoDTO);
        }
        return Optional.empty();
    }

    @GetMapping("/pedido/{pedidoId}/puesto/{puestoId}")
    public ResponseEntity<List<DetallePedidoDTO>> getDetallesPedidoByPedidoIdAndPuestoId(@PathVariable Long pedidoId, @PathVariable Long puestoId) {
        List<DetallePedidoDTO> detallesPedido = detallePedidoServicio.getDetallesPedidoByPedidoIdAndPuestoId(pedidoId, puestoId);
        return ResponseEntity.ok(detallesPedido);
    }

    @PostMapping("/puesto/{puestoId}")
    public ResponseEntity<DetallePedidoDTO> createDetallePedidoForPuesto(
            @PathVariable Long puestoId,
            @Valid @RequestBody CreateDetallePedidoDTO createDetallePedidoDTO) {
        DetallePedidoDTO newDetallePedido = detallePedidoServicio.createDetallePedidoForPuesto(puestoId, createDetallePedidoDTO);
        return new ResponseEntity<>(newDetallePedido, HttpStatus.CREATED);
    }











}
