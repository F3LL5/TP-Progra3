package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.Pedido.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoServicio {
    List<PedidoDTO> getAllPedidos();
    Optional<PedidoDTO> getPedidoById(Long id);
    PedidoDTO createPedido(CreatePedidoDTO createPedidoDTO);
    Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO);
    boolean deletePedido(Long id);
    List<PedidoDTO> filtrarYOrdenar(String tipo_transaccion, LocalDate fechaMin, LocalDate fechaMax, String sortBy, String sortDir);
    List<PedidoDTO> getPedidosByPuestoId(Long puestoId);
    List<FacturaDTO> generarFactura(Long id);
    Optional<PedidoDTO> getPedidoByIdAndPuestoId(Long id, Long puestoId);

    @Transactional
    PedidoDTO createPedidoYtransaccion(CreatePedidoDTO2 createPedidoDTO2);

    @Transactional
    boolean deletePedidoFromPuesto(Long id, Long puestoId);

    @Transactional
    Optional<PedidoDTO> updatePedidoForPuesto(Long id, Long puestoId, UpdatePedidoDTO updatePedidoDTO);
}
