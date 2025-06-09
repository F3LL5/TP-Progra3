package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;

import java.util.List;
import java.util.Optional;

public interface DetallePedidoServicio {
    List<DetallePedidoDTO> getAllDetallesPedido();
    Optional<DetallePedidoDTO> getDetallePedidoById(Long id);
    DetallePedidoDTO createDetallePedido(CreateDetallePedidoDTO createDetallePedidoDTO);
    Optional<DetallePedidoDTO> updateDetallePedido(Long id, UpdateDetallePedidoDTO updateDetallePedidoDTO);
    boolean deleteDetallePedido(Long id);
}
