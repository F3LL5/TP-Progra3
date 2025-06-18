package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Item.modelo.Item;
import com.owo.TP_prg3.Clases.Item.modelo.ItemRepositorio;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    TransaccionRepositorio transaccionRepositorio;


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

        //Me fijo si el itemId que está en el DP está en el puesto y obtengo la info del inventario de ese itemId.
        Optional<InventarioPuesto> inventarioItem = inventarioPuestoRepositorio.findAll()
                .stream()
                .filter(inv ->
                        inv.getPuesto().getPuestoId().equals(optionalPedido.get().getPuestoId()) &&
                                inv.getItemId().equals(optionalItem.get().getItem_id()))
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

        updateTransaccionMontoForPedido(savedDetallePedido.getPedido().getPedidoId());

        return convertirA_DTO(savedDetallePedido);
    }

    @Override
    public Optional<DetallePedidoDTO> updateDetallePedido(Long id, UpdateDetallePedidoDTO updateDetallePedidoDTO) {
        return detallePedidoRepositorio.findById(id)
                .map(detallePedido -> {
                    if (detallePedido == null) {
                        throw new RecursoNoEncontradoException("DetallePedido con ID " + id + " no encontrado para actualizar.");
                    }


                    if (updateDetallePedidoDTO.getCantidad() != null) {
                        detallePedido.setCantidad(updateDetallePedidoDTO.getCantidad());

                        Optional<InventarioPuesto> inventarioItem = inventarioPuestoRepositorio.findAll()
                                .stream()
                                .filter(inv ->
                                        inv.getPuesto().getPuestoId().equals(detallePedido.getPedido().getPuestoId()) &&
                                                inv.getItemId().equals(detallePedido.getItem().getItem_id()))
                                .findFirst();

                        if (inventarioItem.isEmpty()) {
                            throw new EntityNotFoundException("InventarioPuesto no encontrado para el Item ID: " + detallePedido.getItem().getItem_id() + " y Puesto ID: " + detallePedido.getPedido().getPuestoId());
                        }

                        BigDecimal cantidad_BD = new BigDecimal(detallePedido.getCantidad());
                        BigDecimal precioVenta = inventarioItem.get().getPrecioVenta();
                        detallePedido.setPrecioTotal(cantidad_BD.multiply(precioVenta));
                    }

                    DetallePedido detallePedidoModificado = detallePedidoRepositorio.save(detallePedido);

                    updateTransaccionMontoForPedido(detallePedidoModificado.getPedido().getPedidoId());

                    return convertirA_DTO(detallePedidoModificado);
                })
                .or(() -> {
                    throw new RecursoNoEncontradoException("DetallePedido con ID " + id + " no encontrado para actualizar.");
                });
    }

    @Override
    @Transactional
    public boolean deleteDetallePedido(Long id) {
        Optional<DetallePedido> optionalDetallePedido = detallePedidoRepositorio.findById(id);

        if (optionalDetallePedido.isPresent()) {
            DetallePedido detallePedidoToDelete = optionalDetallePedido.get();
            Long pedidoId = detallePedidoToDelete.getPedido().getPedidoId(); // Obtener el ID del Pedido asociado

            detallePedidoRepositorio.deleteById(id);

            // Después de eliminar el DetallePedido, actualizar el monto de la Transaccion asociada
            updateTransaccionMontoForPedido(pedidoId);

            return true;
        }
        return false;
    }

    public List<DetallePedidoDTO> getDetallesPedidoByPedidoIdAndPuestoId(Long pedidoId, Long puestoId) {
        return detallePedidoRepositorio.findAll().stream()
                .filter(detalle -> detalle.getPedido() != null && detalle.getPedido().getPedidoId().equals(pedidoId) && detalle.getPedido().getPuestoId().equals(puestoId))
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    public Optional<DetallePedidoDTO> getDetallePedidoByIdAndPuestoId(Long id, Long puestoId) {
        return detallePedidoRepositorio.findById(id)
                .filter(detalle -> detalle.getPedido() != null && detalle.getPedido().getPuestoId().equals(puestoId))
                .map(this::convertirA_DTO);
    }

    @Transactional
    public DetallePedidoDTO createDetallePedidoForPuesto(Long puestoId, CreateDetallePedidoDTO createDetallePedidoDTO) {
        // 1. Verificar que el Pedido exista y pertenezca al Puesto
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(createDetallePedidoDTO.getPedidoId());

        if (optionalPedido.isEmpty()) {
            throw new RecursoNoEncontradoException("Pedido con ID " + createDetallePedidoDTO.getPedidoId() + " no encontrado.");
        }

        Pedido pedido = optionalPedido.get();

        if (!pedido.getPuestoId().equals(puestoId)) {
            throw new RecursoNoEncontradoException("El Pedido con ID " + createDetallePedidoDTO.getPedidoId() + " no pertenece al Puesto con ID " + puestoId + ".");
        }







        // 2. Si las verificaciones son exitosas, crea el detalle de pedido usando el método existente
        return createDetallePedido(createDetallePedidoDTO);
    }

    @Transactional
    private void updateTransaccionMontoForPedido(Long pedidoId) {
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(pedidoId);
        if (optionalPedido.isEmpty()) {
            // This should ideally not happen if the Pedido was valid when DetallePedido was created/updated/deleted
            throw new RecursoNoEncontradoException("Pedido con ID " + pedidoId + " no encontrado para actualizar Transaccion.");
        }

        Pedido pedido = optionalPedido.get();
        Transaccion transaccion = pedido.getTransaccion();

        if (transaccion == null) {
            throw new RecursoNoEncontradoException("Transaccion asociada al Pedido con ID " + pedidoId + " no encontrada.");
        }

        // Calculate the sum of precioTotal for all DetallePedidos linked to this Pedido
        BigDecimal totalMontoPedido = detallePedidoRepositorio.findByPedido(pedido).stream()
                .map(DetallePedido::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        transaccion.setMonto(totalMontoPedido);
        transaccionRepositorio.save(transaccion);
    }











}



