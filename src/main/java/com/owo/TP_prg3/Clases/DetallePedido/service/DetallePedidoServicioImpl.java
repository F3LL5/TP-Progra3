package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TipoTransaccion;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuesto;
import com.owo.TP_prg3.Clases.InventarioPuesto.modelo.InventarioPuestoRepositorio;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import com.owo.TP_prg3.Clases.Producto.modelo.ProductoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.service.TransaccionServicioImpl;
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
    private ProductoRepositorio itemRepositorio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private InventarioPuestoRepositorio inventarioPuestoRepositorio;
    @Autowired
    TransaccionRepositorio transaccionRepositorio;
    @Autowired
    TransaccionServicioImpl transaccionServicio;

    private DetallePedidoDTO convertirA_DTO(DetallePedido detallePedido) {
        return new DetallePedidoDTO(
                detallePedido.getDetallePedidoId(),
                detallePedido.getPedido() != null ? detallePedido.getPedido().getPedidoId() : null, // Obtener ID del pedido
                detallePedido.getProducto() != null ? detallePedido.getProducto().getProducto_id() : null,
                detallePedido.getCantidad(),
                detallePedido.getPrecioTotal()
        );
    }

    private DetallePedido convertirA_DetallePedido(CreateDetallePedidoDTO detallePedidoDTO) {
        DetallePedido detallePedido = new DetallePedido();

        Optional<Producto> optionalItem = itemRepositorio.findById(detallePedidoDTO.getItemId());
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(detallePedidoDTO.getPedidoId());

        if (optionalItem.isEmpty() || optionalPedido.isEmpty()) throw new RecursoNoEncontradoException("Item/Pedido con ID " + detallePedidoDTO.getItemId() + " no encontrado.");

        Pedido pedido = optionalPedido.get();
        detallePedido.setPedido(pedido);
        detallePedido.setProducto(optionalItem.get());

        //Me fijo si el itemId que está en el DP está en el puesto y obtengo la info del inventario de ese itemId.
        Optional<InventarioPuesto> inventarioItem = inventarioPuestoRepositorio.findAll()
                .stream()
                .filter(inv ->
                        inv.getPuesto().getPuestoId().equals(pedido.getPuestoId()) &&
                                inv.getItemId().equals(optionalItem.get().getProducto_id()))
                .findFirst();

        if (inventarioItem.isEmpty()) throw new RecursoNoEncontradoException("InventarioPuesto no encontrado para el Item ID: " + detallePedidoDTO.getItemId() + " y Puesto ID: " + detallePedido.getPedido().getPuestoId());

        detallePedido.setCantidad(detallePedidoDTO.getCantidad());

        BigDecimal cantidad_BD = new BigDecimal(detallePedido.getCantidad());


        TipoTransaccion tipoTransaccion = pedido.getTransaccion().getTipo();
        BigDecimal precioUnitario;


        if (tipoTransaccion == TipoTransaccion.VENTA) {
            precioUnitario = inventarioItem.get().getPrecioVenta();
        } else if (tipoTransaccion == TipoTransaccion.COMPRA) {
            precioUnitario = inventarioItem.get().getCostoAdquisicion();
        } else {

            throw new IngresoInvalidoException("No se pueden agregar items a pedidos de tipo " + tipoTransaccion);
        }

        // Usamos el precio unitario correcto para el cálculo
        detallePedido.setPrecioTotal(cantidad_BD.multiply(precioUnitario));


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
        return Optional.of(detallePedidoRepositorio.findById(id).map(this::convertirA_DTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido con ID " + id + " no encontrado.")));
    }

    @Override
    @Transactional
    public DetallePedidoDTO createDetallePedido(CreateDetallePedidoDTO createDetallePedidoDTO) {
        // Obtener el pedido antes de crear el detalle para obtener el monto de la transacción
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(createDetallePedidoDTO.getPedidoId());
        if (optionalPedido.isEmpty()) {
            throw new RecursoNoEncontradoException("Pedido con ID " + createDetallePedidoDTO.getPedidoId() + " no encontrado.");
        }

        if (createDetallePedidoDTO.getCantidad() <= 0) {
            throw new IngresoInvalidoException("La cantidad debe ser mayor a cero.");
        }

        Pedido pedido = optionalPedido.get();

        // Validación: Verificar existencia del Item
        Optional<Producto> optionalItem = itemRepositorio.findById(createDetallePedidoDTO.getItemId());
        if (optionalItem.isEmpty()) {
            throw new RecursoNoEncontradoException("Item con ID " + createDetallePedidoDTO.getItemId() + " no encontrado.");
        }

        // Obtener el monto actual de la Transacción ANTES de agregar el nuevo detalle
        BigDecimal previousMontoTransaccion = BigDecimal.ZERO;
        if (pedido.getTransaccion() != null) {
            previousMontoTransaccion = pedido.getTransaccion().getMonto();
        }

        DetallePedido nuevoDetallePedido = convertirA_DetallePedido(createDetallePedidoDTO);
        DetallePedido savedDetallePedido = detallePedidoRepositorio.save(nuevoDetallePedido);

        // Recalcular el monto total del pedido y actualizar la transacción y los saldos
        updateTransaccionMontoForPedido(savedDetallePedido.getPedido().getPedidoId(), previousMontoTransaccion);


        actualizarStockInventarioPuesto(
                savedDetallePedido.getDetallePedidoId(),
                savedDetallePedido.getPedido().getPuestoId(),
                savedDetallePedido.getCantidad(),
                pedido.getTransaccion().getTipo()
        );

        return convertirA_DTO(savedDetallePedido);
    }

    @Override
    @Transactional
    public Optional<DetallePedidoDTO> updateDetallePedido(Long id, UpdateDetallePedidoDTO updateDetallePedidoDTO) {
        return detallePedidoRepositorio.findById(id)
                .map(detallePedido -> {
                    if (detallePedido == null) throw new RecursoNoEncontradoException("DetallePedido con ID " + id + " no encontrado para actualizar.");

                    if (updateDetallePedidoDTO == null ||
                            (updateDetallePedidoDTO.getCantidad() != null && updateDetallePedidoDTO.getCantidad() <= 0)) {
                        throw new IngresoInvalidoException("La cantidad debe ser mayor a cero si se proporciona.");
                    }

                    Integer cantidadAnterior = detallePedido.getCantidad();

                    BigDecimal montoAnteriorTransaccion = BigDecimal.ZERO;
                    if (detallePedido.getPedido() != null && detallePedido.getPedido().getTransaccion() != null) {
                        montoAnteriorTransaccion = detallePedido.getPedido().getTransaccion().getMonto();
                    }

                    if (updateDetallePedidoDTO.getCantidad() != null) {
                        detallePedido.setCantidad(updateDetallePedidoDTO.getCantidad());

                        Optional<InventarioPuesto> inventarioItem = inventarioPuestoRepositorio.findAll()
                                .stream()
                                .filter(inv -> inv.getPuesto().getPuestoId().equals(detallePedido.getPedido().getPuestoId()) && inv.getItemId().equals(detallePedido.getProducto().getProducto_id()))
                                .findFirst();

                        if (inventarioItem.isEmpty()) throw new RecursoNoEncontradoException("InventarioPuesto no encontrado para el Item ID: " + detallePedido.getProducto().getProducto_id() + " y Puesto ID: " + detallePedido.getPedido().getPuestoId());


                        BigDecimal cantidad_BD = new BigDecimal(detallePedido.getCantidad());
                        TipoTransaccion tipoTransaccion = detallePedido.getPedido().getTransaccion().getTipo();
                        BigDecimal precioUnitario;

                        if (tipoTransaccion == TipoTransaccion.VENTA) {
                            precioUnitario = inventarioItem.get().getPrecioVenta();
                        } else if (tipoTransaccion == TipoTransaccion.COMPRA) {
                            precioUnitario = inventarioItem.get().getCostoAdquisicion();
                        } else {
                            throw new IngresoInvalidoException("No se pueden agregar items a pedidos de tipo " + tipoTransaccion);
                        }

                        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new RecursoNoEncontradoException("El precio (venta o adquisición) debe ser mayor a cero.");
                        }

                        detallePedido.setPrecioTotal(cantidad_BD.multiply(precioUnitario));

                    }

                    DetallePedido detallePedidoModificado = detallePedidoRepositorio.save(detallePedido);

                    updateTransaccionMontoForPedido(detallePedidoModificado.getPedido().getPedidoId(), montoAnteriorTransaccion);

                    Integer diferenciaCantidad = detallePedidoModificado.getCantidad() - cantidadAnterior;
                    if (diferenciaCantidad != 0) {
                        actualizarStockInventarioPuesto(
                                detallePedidoModificado.getProducto().getProducto_id(),
                                detallePedidoModificado.getPedido().getPuestoId(),
                                diferenciaCantidad,
                                detallePedido.getPedido().getTransaccion().getTipo()
                        );
                    }

                    return convertirA_DTO(detallePedidoModificado);

                }).or(() -> { throw new RecursoNoEncontradoException("DetallePedido con ID " + id + " no encontrado para actualizar.");});
    }

    @Override
    @Transactional
    public boolean deleteDetallePedido(Long id) {
        Optional<DetallePedido> optionalDetallePedido = detallePedidoRepositorio.findById(id);

        if (optionalDetallePedido.isPresent()) {
            DetallePedido detallePedidoToDelete = optionalDetallePedido.get();
            Long pedidoId = detallePedidoToDelete.getPedido().getPedidoId();
            Long itemId = detallePedidoToDelete.getProducto().getProducto_id();
            Long puestoId = detallePedidoToDelete.getPedido().getPuestoId();
            Integer cantidadEliminada = detallePedidoToDelete.getCantidad();
            TipoTransaccion tipoTransaccion = detallePedidoToDelete.getPedido().getTransaccion().getTipo();

            BigDecimal previousMontoTransaccion = BigDecimal.ZERO;
            if (detallePedidoToDelete.getPedido() != null && detallePedidoToDelete.getPedido().getTransaccion() != null) {
                previousMontoTransaccion = detallePedidoToDelete.getPedido().getTransaccion().getMonto();
            }

            detallePedidoRepositorio.deleteById(id);

            updateTransaccionMontoForPedido(pedidoId, previousMontoTransaccion);



            actualizarStockInventarioPuesto(
                    itemId,
                    puestoId,
                    -cantidadEliminada, // Se resta lo que se vendio, o se suma lo que se compro
                    tipoTransaccion
            );

            return true;
        } else throw new RecursoNoEncontradoException("DetallePedido con ID " + id + " no encontrado para eliminar.");
    }

    @Override
    public List<DetallePedidoDTO> getDetallesPedidoByPedidoIdAndPuestoId(Long pedidoId, Long puestoId) {
        return detallePedidoRepositorio.findAll().stream()
                .filter(detalle -> detalle.getPedido() != null && detalle.getPedido().getPedidoId().equals(pedidoId) && detalle.getPedido().getPuestoId().equals(puestoId))
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DetallePedidoDTO> getDetallePedidoByIdAndPuestoId(Long id, Long puestoId) {
        return detallePedidoRepositorio.findById(id)
                .filter(detalle -> detalle.getPedido() != null && detalle.getPedido().getPuestoId().equals(puestoId))
                .map(this::convertirA_DTO);
    }

    @Transactional
    @Override
    public DetallePedidoDTO createDetallePedidoForPuesto(Long puestoId, CreateDetallePedidoDTO createDetallePedidoDTO) {
        // 1. Verificar que el Pedido exista y pertenezca al Puesto
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(createDetallePedidoDTO.getPedidoId());

        if (optionalPedido.isEmpty()) throw new RecursoNoEncontradoException("Pedido con ID " + createDetallePedidoDTO.getPedidoId() + " no encontrado.");

        Pedido pedido = optionalPedido.get();

        if (!pedido.getPuestoId().equals(puestoId)) throw new RecursoNoEncontradoException("El Pedido con ID " + createDetallePedidoDTO.getPedidoId() + " no pertenece al Puesto con ID " + puestoId + ".");

        if (createDetallePedidoDTO.getCantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");

        Optional<Producto> optionalItem = itemRepositorio.findById(createDetallePedidoDTO.getItemId());
        if (optionalItem.isEmpty()) {
            throw new RecursoNoEncontradoException("Item con ID " + createDetallePedidoDTO.getItemId() + " no encontrado.");
        }

        // 2. Si las verificaciones son exitosas, crea el detalle de pedido
        return createDetallePedido(createDetallePedidoDTO);
    }

    @Transactional
    @Override
    public void updateTransaccionMontoForPedido(Long pedidoId, BigDecimal previousMonto) {
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(pedidoId);
        if (optionalPedido.isEmpty()) throw new RecursoNoEncontradoException("Pedido con ID " + pedidoId + " no encontrado para actualizar Transaccion.");

        Pedido pedido = optionalPedido.get();
        Transaccion transaccion = pedido.getTransaccion();

        if (transaccion == null) {
            throw new RecursoNoEncontradoException("Transaccion asociada al Pedido con ID " + pedidoId + " no encontrada.");
        }

        // Calcular la suma de precioTotal para todos los DetallePedidos vinculados a este Pedido
        BigDecimal totalMontoPedido = detallePedidoRepositorio.findByPedido(pedido).stream()
                .map(DetallePedido::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Si el monto no ha cambiado, no hacemos nada para evitar operaciones innecesarias en las cuentas
        if (totalMontoPedido.compareTo(previousMonto) == 0) {
            return;
        }

        // Primero, actualizamos el monto de la Transaccion en la DB
        transaccion.setMonto(totalMontoPedido);
        transaccionRepositorio.save(transaccion);


        // Luego, llamamos al servicio de Transaccion para que ajuste los saldos
        transaccionServicio.ajustarSaldosPorCambioDeMonto(transaccion.getTransaccionId(), previousMonto, totalMontoPedido);
    }

    @Transactional
    @Override
    public void actualizarStockInventarioPuesto(Long itemId, Long puestoId, Integer cantidadAjuste, TipoTransaccion tipo) {
        Optional<InventarioPuesto> optionalInventarioPuesto = inventarioPuestoRepositorio.findAll()
                .stream()
                .filter(inv ->
                        inv.getPuesto().getPuestoId().equals(puestoId) &&
                                inv.getItemId().equals(itemId))
                .findFirst();

        if (optionalInventarioPuesto.isEmpty()) {
            throw new EntityNotFoundException("InventarioPuesto no encontrado para el Item ID: " + itemId + " y Puesto ID: " + puestoId);
        }

        InventarioPuesto inventarioPuesto = optionalInventarioPuesto.get();
        Integer nuevaCantidad;


        if (tipo == TipoTransaccion.VENTA) {
            // En una venta, se RESTA del stock
            nuevaCantidad = inventarioPuesto.getCantidad() - cantidadAjuste;
            if (nuevaCantidad < 0) {
                throw new IllegalArgumentException("Stock insuficiente para el item ID: " + itemId + " en el puesto ID: " + puestoId);
            }
        } else if (tipo == TipoTransaccion.COMPRA) {
            // En una compra, se SUMA al stock
            nuevaCantidad = inventarioPuesto.getCantidad() + cantidadAjuste;
        } else {
            // No hacer nada si es otro tipo de transacción que no afecta stock
            return;
        }

        inventarioPuesto.setCantidad(nuevaCantidad);
        inventarioPuestoRepositorio.save(inventarioPuesto);
    }

    @Override
    public BigDecimal calcularTotalVenta(Long pedidoId) {
        Optional<Pedido> optionalPedido = pedidoRepositorio.findById(pedidoId);
        if (optionalPedido.isEmpty()) {
            throw new RecursoNoEncontradoException("Pedido con ID " + pedidoId + " no encontrado.");
        }

        Pedido pedido = optionalPedido.get();
        return detallePedidoRepositorio.findByPedido(pedido).stream()
                .map(DetallePedido::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



}



