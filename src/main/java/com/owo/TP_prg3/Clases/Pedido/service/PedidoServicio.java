package com.owo.TP_prg3.Clases.Pedido.service;

import com.owo.TP_prg3.Clases.Pedido.dto.CreatePedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.PedidoDTO;
import com.owo.TP_prg3.Clases.Pedido.dto.UpdatePedidoDTO;

import java.util.List;
import java.util.Optional;

public interface PedidoServicio {
    List<PedidoDTO> getAllPedidos();
    Optional<PedidoDTO> getPedidoById(Long id);
    PedidoDTO createPedido(CreatePedidoDTO createPedidoDTO);
    Optional<PedidoDTO> updatePedido(Long id, UpdatePedidoDTO updatePedidoDTO);
    boolean deletePedido(Long id);
    List<PedidoDTO> filtrarYOrdenar(String tipo_transaccion, String sortBy, String sortDir);
}
