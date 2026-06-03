package com.owo.TP_prg3.Clases.DetallePedido.historial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialDetallePedidoRepositorio extends JpaRepository<HistorialDetallePedido, Long> {
    List<HistorialDetallePedido> findAllByOrderByFechaEventoDesc();
}
