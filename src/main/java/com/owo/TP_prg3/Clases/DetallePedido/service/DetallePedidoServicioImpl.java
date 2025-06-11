package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoServicioImpl implements DetallePedidoServicio {

    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;
    @Autowired
    private ItemRepositorio itemRepositorio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    private DetallePedidoDTO convertirA_DTO(DetallePedido detallePedido) {
        return new DetallePedidoDTO(
                detallePedido.getDetallePedidoId(),
                detallePedido.getPedido() != null ? detallePedido.getPedido().getPedidoId() : null, // Obtener ID del pedido
                detallePedido.getItem() != null ? detallePedido.getItem().getItem_id() : null,
                detallePedido.getCantidad(),
                detallePedido.getPrecioTotal()
        );
    }

    private DetallePedido convertirA_DetallePedido(CreateDetallePedidoDTO detallePedidoDTO) {
        DetallePedido detallePedido = new DetallePedido();

        itemRepositorio.findById(detallePedidoDTO.getItemId())
                .ifPresentOrElse(
                        detallePedido::setItem,
                        () -> { throw new EntityNotFoundException("Item con ID " + detallePedidoDTO.getItemId() + " no encontrado."); }
                );

        detallePedido.setCantidad(detallePedidoDTO.getCantidad());
        detallePedido.setPrecioTotal(detallePedidoDTO.getPrecioTotal());
        return detallePedido;
    }

    @Override
    public List<DetallePedidoDTO> getAllDetallesPedido() {
        return detallePedidoRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<DetallePedidoDTO> getDetallePedidoById(Long id) {
        return detallePedidoRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    public DetallePedidoDTO createDetallePedido(CreateDetallePedidoDTO createDetallePedidoDTO) {
        DetallePedido nuevoDetallePedido = new DetallePedido();

        // Buscar y establecer el Pedido
        pedidoRepositorio.findById(createDetallePedidoDTO.getPedidoId())
                .ifPresentOrElse(
                        nuevoDetallePedido::setPedido,
                        () -> { throw new EntityNotFoundException("Pedido con ID " + createDetallePedidoDTO.getPedidoId() + " no encontrado."); }
                );

        // Buscar y establecer el Item
        itemRepositorio.findById(createDetallePedidoDTO.getItemId())
                .ifPresentOrElse(
                        nuevoDetallePedido::setItem,
                        () -> { throw new EntityNotFoundException("Item con ID " + createDetallePedidoDTO.getItemId() + " no encontrado."); }
                );

        nuevoDetallePedido.setCantidad(createDetallePedidoDTO.getCantidad());
        nuevoDetallePedido.setPrecioTotal(createDetallePedidoDTO.getPrecioTotal());

        DetallePedido savedDetallePedido = detallePedidoRepositorio.save(nuevoDetallePedido);
        return convertirA_DTO(savedDetallePedido);
    }

    @Override
    public Optional<DetallePedidoDTO> updateDetallePedido(Long id, UpdateDetallePedidoDTO updateDetallePedidoDTO) {
        return detallePedidoRepositorio.findById(id)
                .map(detallePedido -> {
                    // Actualizar el Pedido si se proporciona
                    if (updateDetallePedidoDTO.getPedidoId() != null) {
                        pedidoRepositorio.findById(updateDetallePedidoDTO.getPedidoId())
                                .ifPresentOrElse(
                                        detallePedido::setPedido,
                                        () -> { throw new EntityNotFoundException("Pedido con ID " + updateDetallePedidoDTO.getPedidoId() + " no encontrado."); }
                                );
                    }

                    // Actualizar el Item si se proporciona
                    if (updateDetallePedidoDTO.getItemId() != null) {
                        itemRepositorio.findById(updateDetallePedidoDTO.getItemId())
                                .ifPresentOrElse(
                                        detallePedido::setItem,
                                        () -> { throw new EntityNotFoundException("Item con ID " + updateDetallePedidoDTO.getItemId() + " no encontrado."); }
                                );
                    }
                    if (updateDetallePedidoDTO.getCantidad() != null) {
                        detallePedido.setCantidad(updateDetallePedidoDTO.getCantidad());
                    }
                    if (updateDetallePedidoDTO.getPrecioTotal() != null) {
                        detallePedido.setPrecioTotal(updateDetallePedidoDTO.getPrecioTotal());
                    }
                    DetallePedido updatedDetallePedido = detallePedidoRepositorio.save(detallePedido);
                    return convertirA_DTO(updatedDetallePedido);
                });
    }

    @Override
    public boolean deleteDetallePedido(Long id) {
        if (detallePedidoRepositorio.existsById(id)) {
            detallePedidoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
