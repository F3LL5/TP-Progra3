package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Item.modelo.Item;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
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
    @Autowired
    private InventarioPuestoRepositorio inventarioPuestoRepositorio;

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

        Optional<Item> optionalItem = itemRepositorio.findById(detallePedidoDTO.getItemId());
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(detallePedidoDTO.getPedidoId());

        if (optionalItem.isEmpty() || optionalPedido.isEmpty()) throw new EntityNotFoundException("Item/Pedido con ID " + detallePedidoDTO.getItemId() + " no encontrado.");

        detallePedido.setPedido(optionalPedido.get());
        detallePedido.setItem(optionalItem.get());

        //Me fijo si el item que está en el DP está en el puesto y obtengo la info del inventario de ese item.
        Optional<InventarioPuesto> inventarioItem = inventarioPuestoRepositorio.findAll()
                .stream()
                .filter(inv ->
                        inv.getPuesto().getPuestoId() == optionalPedido.get().getPuestoId() &&
                        inv.getItem().getItem_id() == optionalItem.get().getItem_id())
                .findFirst();

        if (inventarioItem.isEmpty()) throw new EntityNotFoundException("InventarioPuesto no encontrado para el Item ID: " + detallePedidoDTO.getItemId() + " y Puesto ID: " + detallePedido.getPedido().getPuestoId());

        detallePedido.setCantidad(detallePedidoDTO.getCantidad());

        BigDecimal cantidad_BD = new BigDecimal(detallePedido.getCantidad());
        BigDecimal precioVenta = inventarioItem.get().getPrecioVenta();

        detallePedido.setPrecioTotal(cantidad_BD.multiply(precioVenta));

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
        DetallePedido nuevoDetallePedido = convertirA_DetallePedido(createDetallePedidoDTO);
        DetallePedido savedDetallePedido = detallePedidoRepositorio.save(nuevoDetallePedido);

        return convertirA_DTO(savedDetallePedido);
    }

    @Override
    public Optional<DetallePedidoDTO> updateDetallePedido(Long id, UpdateDetallePedidoDTO updateDetallePedidoDTO) {
        return detallePedidoRepositorio.findById(id)
                .map(detallePedido -> {
                    boolean recalcular = false;

                    // Actualizar el Pedido si se proporciona
                    if (updateDetallePedidoDTO.getPedidoId() != null) {
                        pedidoRepositorio.findById(updateDetallePedidoDTO.getPedidoId())
                                .ifPresentOrElse(
                                        detallePedido::setPedido,
                                        () -> { throw new EntityNotFoundException("Pedido con ID " + updateDetallePedidoDTO.getPedidoId() + " no encontrado."); }
                                );
                        recalcular = true;
                    }

                    // Actualizar el Item si se proporciona
                    if (updateDetallePedidoDTO.getItemId() != null) {
                        itemRepositorio.findById(updateDetallePedidoDTO.getItemId())
                                .ifPresentOrElse(
                                        detallePedido::setItem,
                                        () -> { throw new EntityNotFoundException("Item con ID " + updateDetallePedidoDTO.getItemId() + " no encontrado."); }
                                );
                        recalcular = true;
                    }

                    // Actualizar la cantidad si se proporciona
                    if (updateDetallePedidoDTO.getCantidad() != null) {
                        detallePedido.setCantidad(updateDetallePedidoDTO.getCantidad());
                        recalcular = true;
                    }

                    //Si el usuario modifico los datos del detalle del pedido hace falta recalcular el precio total.
                    if (recalcular) {
                        if (detallePedido.getItem() != null || detallePedido.getPedido() != null || detallePedido.getCantidad() == null) {
                            throw new IllegalStateException("Faltan datos para recalcular el precio total del detalle del pedido.");
                        }
                        Long puestoId = detallePedido.getPedido().getPuestoId();
                        Long itemId = detallePedido.getItem().getItem_id();

                        Optional<InventarioPuesto> inventarioItem = inventarioPuestoRepositorio.findAll()
                                .stream()
                                .filter(inv -> inv.getPuesto().getPuestoId().equals(puestoId) && inv.getItem().getItem_id().equals(itemId))
                                .findFirst();

                        if (inventarioItem.isEmpty()) {
                            throw new EntityNotFoundException("InventarioPuesto no encontrado para el Item ID: " + itemId + " y Puesto ID: " + puestoId + " durante la actualización.");
                        }

                        BigDecimal cantidad_BD = new BigDecimal(detallePedido.getCantidad());
                        BigDecimal precioVenta = inventarioItem.get().getPrecioVenta();
                        detallePedido.setPrecioTotal(cantidad_BD.multiply(precioVenta));
                    } else if (updateDetallePedidoDTO.getPrecioTotal() != null) {
                        // Si no se necesita recalcular, pero el DTO trae precioTotal, lo modifico directamente
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
