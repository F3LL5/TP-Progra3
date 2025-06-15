package com.owo.TP_prg3.Clases.InventarioPuesto.service;

import com.owo.TP_prg3.Clases.InventarioPuesto.dto.CreateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.InventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.dto.UpdateInventarioPuestoDTO;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import com.owo.TP_prg3.Clases.Puesto.modelo.PuestoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioPuestoServicioImpl implements InventarioPuestoServicio {

    @Autowired
    private InventarioPuestoRepositorio inventarioPuestoRepositorio;
    @Autowired
    private PuestoRepositorio puestoRepositorio;
    @Autowired
    private ItemRepositorio itemRepositorio;

    private InventarioPuestoDTO convertirA_DTO(InventarioPuesto inventarioPuesto) {
        return new InventarioPuestoDTO(
                inventarioPuesto.getInventario_id(),
                inventarioPuesto.getCantidad(),
                inventarioPuesto.getPuesto() != null ? inventarioPuesto.getPuesto().getPuestoId() : null,
                inventarioPuesto.getItem() != null ? inventarioPuesto.getItem().getItem_id() : null,
                inventarioPuesto.getStockMin(),
                inventarioPuesto.getPrecioVenta()
        );
    }

    private InventarioPuesto convertirA_InventarioPuesto(CreateInventarioPuestoDTO inventarioPuestoDTO) {
        InventarioPuesto inventarioPuesto = new InventarioPuesto();
        inventarioPuesto.setCantidad(inventarioPuestoDTO.getCantidad());

        puestoRepositorio.findById(inventarioPuestoDTO.getPuestoId())
                .ifPresentOrElse(
                        inventarioPuesto::setPuesto,
                        () -> { throw new EntityNotFoundException("Puesto con ID " + inventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                );

        itemRepositorio.findById(inventarioPuestoDTO.getItemId())
                .ifPresentOrElse(
                        inventarioPuesto::setItem,
                        () -> { throw new EntityNotFoundException("Item con ID " + inventarioPuestoDTO.getItemId() + " no encontrado."); }
                );

        inventarioPuesto.setStockMin(inventarioPuestoDTO.getStockMin());
        inventarioPuesto.setPrecioVenta(inventarioPuestoDTO.getPrecioVenta());
        return inventarioPuesto;
    }

    @Override
    public List<InventarioPuestoDTO> getAllInventarioPuestos() {
        return inventarioPuestoRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<InventarioPuestoDTO> getInventarioPuestoById(Long id) {
        return inventarioPuestoRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    public InventarioPuestoDTO createInventarioPuesto(CreateInventarioPuestoDTO createInventarioPuestoDTO) {
        InventarioPuesto inventarioPuesto = convertirA_InventarioPuesto(createInventarioPuestoDTO);
        InventarioPuesto savedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
        return convertirA_DTO(savedInventarioPuesto);
    }

    @Override
    public Optional<InventarioPuestoDTO> updateInventarioPuesto(Long id, UpdateInventarioPuestoDTO updateInventarioPuestoDTO) {
        return inventarioPuestoRepositorio.findById(id)
                .map(inventarioPuesto -> {
                    if (updateInventarioPuestoDTO.getCantidad() != null) {
                        inventarioPuesto.setCantidad(updateInventarioPuestoDTO.getCantidad());
                    }
                    if (updateInventarioPuestoDTO.getPuestoId() != null) {
                        puestoRepositorio.findById(updateInventarioPuestoDTO.getPuestoId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setPuesto,
                                        () -> { throw new EntityNotFoundException("Puesto con ID " + updateInventarioPuestoDTO.getPuestoId() + " no encontrado."); }
                                );
                    }
                    if (updateInventarioPuestoDTO.getItemId() != null) {
                        itemRepositorio.findById(updateInventarioPuestoDTO.getItemId())
                                .ifPresentOrElse(
                                        inventarioPuesto::setItem,
                                        () -> { throw new EntityNotFoundException("Item con ID " + updateInventarioPuestoDTO.getItemId() + " no encontrado."); }
                                );
                    }
                    if (updateInventarioPuestoDTO.getStockMin() != null) {
                        inventarioPuesto.setStockMin(updateInventarioPuestoDTO.getStockMin());
                    }
                    if (updateInventarioPuestoDTO.getPrecioVenta() != null) {
                        inventarioPuesto.setPrecioVenta(updateInventarioPuestoDTO.getPrecioVenta());
                    }
                    InventarioPuesto updatedInventarioPuesto = inventarioPuestoRepositorio.save(inventarioPuesto);
                    return convertirA_DTO(updatedInventarioPuesto);
                });
    }

    @Override
    public boolean deleteInventarioPuesto(Long id) {
        if (inventarioPuestoRepositorio.existsById(id)) {
            inventarioPuestoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
