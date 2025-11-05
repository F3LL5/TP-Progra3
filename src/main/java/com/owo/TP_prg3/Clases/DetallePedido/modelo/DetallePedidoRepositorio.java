package com.owo.TP_prg3.Clases.DetallePedido.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface DetallePedidoRepositorio extends JpaRepository<DetallePedido, Long> {
    Set<DetallePedido> findByPedido_PedidoId(Long pedidoId);
}
