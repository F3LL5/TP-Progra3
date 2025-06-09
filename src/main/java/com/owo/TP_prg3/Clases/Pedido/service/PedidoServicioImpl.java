package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Pedido.modelo.PedidoRepositorio;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoServicioImpl implements PedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private DetallePedidoRepositorio detallePedidoRepositorio;

    private PedidoDTO convertirA_DTO(Pedido pedido) {
        return new PedidoDTO(
                pedido.getPedidoId(),
                pedido.getTransaccion() != null ? pedido.getTransaccion().getTransaccionId() : null,
                pedido.getDetallePedido() != null ? pedido.getDetallePedido().getDetallePedidoId() : null
        );
    }

    private Pedido convertirA_Pedido(CreatePedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();

        transaccionRepositorio.findById(pedidoDTO.getTransaccionId())
                .ifPresentOrElse(
                        pedido::setTransaccion,
                        () -> { throw new EntityNotFoundException("Transaccion con ID " + pedidoDTO.getTransaccionId() + " no encontrada."); }
                );

        detallePedidoRepositorio.findById(pedidoDTO.getDetallePedidoId())
                .ifPresentOrElse(
                        pedido::setDetallePedido,
                        () -> { throw new EntityNotFoundException("DetallePedido con ID " + pedidoDTO.getDetallePedidoId() + " no encontrado."); }
                );

        return pedido;
    }

    @Override
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<PedidoDTO> getPedidoById(Long id) {
        return pedidoRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    @Transactional
    public PedidoDTO createPedido(CreatePedidoDTO createPedidoDTO) {
        Pedido pedido = convertirA_Pedido(createPedidoDTO);
        Pedido savedPedido = pedidoRepositorio.save(pedido);
        return convertirA_DTO(savedPedido);
    }

    @Override
    @Transactional
    public Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO) {
        return pedidoRepositorio.findById(id)
                .map(pedido -> {
                    if (updatePedidoDTO.getTransaccionId() != null) {
                        transaccionRepositorio.findById(updatePedidoDTO.getTransaccionId())
                                .ifPresentOrElse(
                                        pedido::setTransaccion,
                                        () -> { throw new EntityNotFoundException("Transaccion con ID " + updatePedidoDTO.getTransaccionId() + " no encontrada."); }
                                );
                    } else if (pedido.getTransaccion() != null) { // Handle explicit null to dissociate
                        pedido.setTransaccion(null);
                    }

                    if (updatePedidoDTO.getDetallePedidoId() != null) {
                        detallePedidoRepositorio.findById(updatePedidoDTO.getDetallePedidoId())
                                .ifPresentOrElse(
                                        pedido::setDetallePedido,
                                        () -> { throw new EntityNotFoundException("DetallePedido con ID " + updatePedidoDTO.getDetallePedidoId() + " no encontrado."); }
                                );
                    } else if (pedido.getDetallePedido() != null) { // Handle explicit null to dissociate
                        pedido.setDetallePedido(null);
                    }

                    Pedido updatedPedido = pedidoRepositorio.save(pedido);
                    return convertirA_DTO(updatedPedido);
                });
    }

    @Override
    public boolean deletePedido(Long id) {
        if (pedidoRepositorio.existsById(id)) {
            pedidoRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
