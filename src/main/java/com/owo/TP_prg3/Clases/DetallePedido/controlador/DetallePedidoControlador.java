package com.owo.TP_prg3.Clases.DetallePedido.controlador;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicioImpl;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
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

    ///  GET ------------------------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<DetallePedidoDTO>> getAllDetallesPedido(){
        List<DetallePedidoDTO> detallesPedido = detallePedidoServicio.getAllDetallesPedido();
        return ResponseEntity.ok(detallesPedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoDTO> getDetallePedidoById(@PathVariable Long id){
        DetallePedidoDTO detallePedidoDTO = detallePedidoServicio.getDetallePedidoById(id)
                .orElse(null);
        return ResponseEntity.ok(detallePedidoDTO);
    }

    @GetMapping("/pedido/{pedidoId}/puesto/{puestoId}")
    public ResponseEntity<List<DetallePedidoDTO>> getDetallesPedidoByPedidoIdAndPuestoId(@PathVariable Long pedidoId, @PathVariable Long puestoId) {
        List<DetallePedidoDTO> detallesPedido = detallePedidoServicio.getDetallesPedidoByPedidoIdAndPuestoId(pedidoId, puestoId);
        return ResponseEntity.ok(detallesPedido);
    }

    ///  POST --------------------------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<DetallePedidoDTO> createDetallePedido(@Valid @RequestBody CreateDetallePedidoDTO createDetallePedidoDTO){
        DetallePedidoDTO newEntidad = detallePedidoServicio.createDetallePedido(createDetallePedidoDTO);
        return new ResponseEntity<>(newEntidad, HttpStatus.CREATED);
    }

    @PostMapping("/puesto/{puestoId}")
    public ResponseEntity<DetallePedidoDTO> createDetallePedidoForPuesto(
            @PathVariable Long puestoId,
            @Valid @RequestBody CreateDetallePedidoDTO createDetallePedidoDTO) {
        DetallePedidoDTO newDetallePedido = detallePedidoServicio.createDetallePedidoForPuesto(puestoId, createDetallePedidoDTO);
        return new ResponseEntity<>(newDetallePedido, HttpStatus.CREATED);
    }

    ///  DELETE -----------------------------------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDetallePedido(@PathVariable Long id){
        detallePedidoServicio.deleteDetallePedido(id);
        return ResponseEntity.noContent().build();
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


    ///  PATCH ----------------------------------------------------------------------------------------------------------
    @PatchMapping("/{id}/puesto/{puestoId}")
    public Optional<DetallePedidoDTO> updateDetallePedidoForPuesto(@PathVariable Long id, @PathVariable Long puestoId, @Valid @RequestBody UpdateDetallePedidoDTO updateDetallePedidoDTO){
        // Primero, verifica si el detalle de pedido pertenece al puesto
        Optional<DetallePedidoDTO> detalle = detallePedidoServicio.getDetallePedidoByIdAndPuestoId(id, puestoId);
        if (detalle.isPresent()) {
            return detallePedidoServicio.updateDetallePedido(id, updateDetallePedidoDTO);
        }
        return Optional.empty();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DetallePedidoDTO> updateDetallePedido(@PathVariable Long id, @Valid @RequestBody UpdateDetallePedidoDTO updateDetallePedidoDTO){
        DetallePedidoDTO detallePedidoDTO = detallePedidoServicio.updateDetallePedido(id, updateDetallePedidoDTO)
                .orElseThrow(null);
        return ResponseEntity.ok(detallePedidoDTO);
    }













}
