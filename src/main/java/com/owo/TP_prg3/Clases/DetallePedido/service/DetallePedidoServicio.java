package com.owo.TP_prg3.Clases.DetallePedido.service;

import com.owo.TP_prg3.Clases.DetallePedido.dto.CreateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.DetallePedidoDTO;
import com.owo.TP_prg3.Clases.DetallePedido.dto.UpdateDetallePedidoDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TipoTransaccion;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DetallePedidoServicio {
    List<DetallePedidoDTO> getAllDetallesPedido();
    Optional<DetallePedidoDTO> getDetallePedidoById(Long id);
    DetallePedidoDTO createDetallePedido(CreateDetallePedidoDTO createDetallePedidoDTO);
    Optional<DetallePedidoDTO> updateDetallePedido(Long id, UpdateDetallePedidoDTO updateDetallePedidoDTO);
    boolean deleteDetallePedido(Long id);
    List<DetallePedidoDTO> getDetallesPedidoByPedidoIdAndPuestoId(Long pedidoId, Long puestoId);
    Optional<DetallePedidoDTO> getDetallePedidoByIdAndPuestoId(Long id, Long puestoId);
    BigDecimal calcularTotalVenta(Long pedidoId);

    @Transactional
    DetallePedidoDTO createDetallePedidoForPuesto(Long puestoId, CreateDetallePedidoDTO createDetallePedidoDTO);

    @Transactional
    void updateTransaccionMontoForPedido(Long pedidoId, BigDecimal previousMonto);

    @Transactional
    void actualizarStockInventarioPuesto(Long itemId, Long puestoId, Integer cantidadAjuste, TipoTransaccion tipo);

}
