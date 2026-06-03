package com.owo.TP_prg3.Clases.Pedido.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialPedidoRepositorio extends JpaRepository<HistorialPedido, Long> {
    List<HistorialPedido> findAllByOrderByFechaEventoDesc();
}
