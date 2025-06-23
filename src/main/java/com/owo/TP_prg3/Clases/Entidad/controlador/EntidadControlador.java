package com.owo.TP_prg3.Clases.Entidad.controlador;

import com.owo.TP_prg3.Clases.Entidad.dto.CreateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.EntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.dto.UpdateEntidadDTO;
import com.owo.TP_prg3.Clases.Entidad.service.EntidadServicioImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entidades")
public class EntidadControlador {

    @Autowired
    private EntidadServicioImpl entidadServicio;


    /// GET ------------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<EntidadDTO>> getAllEntidades(){
        List<EntidadDTO> entidades = entidadServicio.getAllEntidades();
        return ResponseEntity.ok(entidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadDTO> getEntidadById(@PathVariable Long id) {
        EntidadDTO entidadDTO = entidadServicio.getEntidadById(id)
                .orElse(null);
        return ResponseEntity.ok(entidadDTO);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<EntidadDTO> getEntidadById(@PathVariable int dni) {
        EntidadDTO entidadDTO = entidadServicio.findByDni(dni)
                .orElse(null);
        return ResponseEntity.ok(entidadDTO);
    }

    @GetMapping("/filtrarYOrdenar")
    public ResponseEntity<List<EntidadDTO>> filtrarYOrdenar(
            @RequestParam(required = false) Long puestoId, @RequestParam(required = false) String rol_entidad,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir
    ) {
        List<EntidadDTO> entidades = entidadServicio.filtrarYOrdenar(puestoId,rol_entidad, sortBy, sortDir);
        return ResponseEntity.ok(entidades);
    }

    @GetMapping("/puesto/{puestoId}")
    public ResponseEntity<List<EntidadDTO>> getEntidadesByPuestoId(@PathVariable Long puestoId) {
        List<EntidadDTO> entidades = entidadServicio.getEntidadesByPuestoId(puestoId);
        return ResponseEntity.ok(entidades);
    }

    // Filtrar Clientes con pedidos de un puesto específico
    @GetMapping("/clientesConPedidosPuesto/{puestoId}")
    public ResponseEntity<List<EntidadDTO>> getClientesConPedidosByPuestoId(@PathVariable Long puestoId) {
        List<EntidadDTO> clientes = entidadServicio.getClientesConPedidosByPuestoId(puestoId);
        return ResponseEntity.ok(clientes);
    }

    /// POST ------------------------------------------------------------------------------------------------------------------------------------------------


    @PostMapping
    public ResponseEntity<EntidadDTO> createEntidad(@Valid @RequestBody CreateEntidadDTO createEntidadDTO){
        EntidadDTO newEntidad = entidadServicio.createEntidad(createEntidadDTO);
        return new ResponseEntity<>(newEntidad, HttpStatus.CREATED);
    }

    // Agregar Entidad para un Puesto específico (asumiendo que el DTO solo tiene los datos de la entidad)
    @PostMapping("/puesto/{puestoId}")
    public ResponseEntity<EntidadDTO> createEntidadForPuesto(@PathVariable Long puestoId, @Valid @RequestBody CreateEntidadDTO createEntidadDTO){
        EntidadDTO newEntidad = entidadServicio.createEntidadForPuesto(puestoId, createEntidadDTO);
        return new ResponseEntity<>(newEntidad, HttpStatus.CREATED);
    }

    @PostMapping("/v2")
    public ResponseEntity<EntidadDTO> createEntidadYCuentaBancaria(
            @Valid @RequestBody CreateEntidadDTO createEntidadDTO) {
        EntidadDTO newEntidad = entidadServicio.createEntidadYCuentaBancaria(createEntidadDTO);
        return new ResponseEntity<>(newEntidad, HttpStatus.CREATED);
    }

    /// DELETE ------------------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntidad(@PathVariable Long id) {
        entidadServicio.deleteEntidad(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar Entidad de un Puesto específico
    @DeleteMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<Void> deleteEntidadFromPuesto(@PathVariable Long id, @PathVariable Long puestoId) {
        entidadServicio.deleteEntidadFromPuesto(id, puestoId);
        return ResponseEntity.noContent().build();
    }


    /// PATCH ------------------------------------------------------------------------------------------------------------------------------------------------

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateEntidadDTO> updateEntidad(@PathVariable Long id, @Valid @RequestBody UpdateEntidadDTO updateEntidadDTO){
        EntidadDTO entidadDTO = entidadServicio.updateEntidad(id, updateEntidadDTO)
                .orElse(null);
        return ResponseEntity.ok(updateEntidadDTO);
    }

    // Modificar Entidad de un Puesto específico
    @PatchMapping("/{id}/puesto/{puestoId}")
    public ResponseEntity<EntidadDTO> updateEntidadForPuesto(@PathVariable Long id, @PathVariable Long puestoId, @Valid @RequestBody UpdateEntidadDTO updateEntidadDTO){
        EntidadDTO entidadDTO = entidadServicio.updateEntidadForPuesto(id, puestoId, updateEntidadDTO)
                .orElse(null);
        return ResponseEntity.ok(entidadDTO);
    }

}