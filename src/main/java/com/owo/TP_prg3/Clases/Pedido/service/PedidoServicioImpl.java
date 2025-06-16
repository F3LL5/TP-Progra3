package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.service.DetallePedidoServicioImpl;
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
import java.util.stream.Collectors;

@Service
public class PedidoServicioImpl implements PedidoServicio {
    //ATRIBUTOS
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    @Autowired
    private DetallePedidoServicioImpl detallePedidoServicio;

    //CONVERSION
    private PedidoDTO convertirA_DTO(Pedido pedido) {
        return new PedidoDTO(
                pedido.getPedidoId(),
                pedido.getTransaccion().getTransaccionId()
        );
    }

    //METODOS
    @Override
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepositorio.findAll().stream()
                .map(this::convertirA_DTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PedidoDTO> getPedidoById(Long id) {
        return pedidoRepositorio.findById(id)
                .map(this::convertirA_DTO);
    }

    @Override
    @Transactional
    public PedidoDTO createPedido(CreatePedidoDTO createPedidoDTO) {
        Pedido nuevoPedido = new Pedido();

        transaccionRepositorio.findById(createPedidoDTO.getTransaccionId())
                .ifPresentOrElse(
                        nuevoPedido::setTransaccion,
                        () -> { throw new EntityNotFoundException("Transaccion con ID " + createPedidoDTO.getTransaccionId() + " no encontrada."); }
                );

        Pedido savedPedido = pedidoRepositorio.save(nuevoPedido);
        return convertirA_DTO(savedPedido);
    }

    @Override
    @Transactional
    public Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO) {
        return pedidoRepositorio.findById(id)
                .map(pedido -> {
                    if (updatePedidoDTO.getTransaccionId() != null) {
                        transaccionRepositorio.findById(updatePedidoDTO.getTransaccionId())
                                .ifPresentOrElse( pedido::setTransaccion, () -> { throw new EntityNotFoundException("Transaccion con ID " + updatePedidoDTO.getTransaccionId() + " no encontrada."); });
                    } else if (pedido.getTransaccion() != null) {
                        pedido.setTransaccion(null);
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
