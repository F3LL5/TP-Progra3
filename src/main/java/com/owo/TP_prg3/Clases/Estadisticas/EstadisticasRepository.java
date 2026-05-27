package com.owo.TP_prg3.Clases.Estadisticas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;

@Repository
public interface EstadisticasRepository extends JpaRepository<Pedido, Long> {

    // Sumar montos de transacciones según el tipo de pedido (VENTA o COMPRA)
    @Query("""
        SELECT SUM(p.transaccion.monto) 
        FROM Pedido p
        WHERE p.tipo = :tipo 
        AND p.transaccion.fecha BETWEEN :inicio AND :fin """)          
    BigDecimal sumMontoPorTipoEnRango(@Param("tipo") Enum<?> tipo, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Obtener ventas agrupadas por el tipo de transacción (EFECTIVO, DEBITO)
    @Query("""
        SELECT p.transaccion.tipo, SUM(p.transaccion.monto) 
        FROM Pedido p 
        WHERE p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA 
        AND p.transaccion.fecha BETWEEN :inicio AND :fin GROUP BY p.transaccion.tipo """)
    List<Object[]> getVentasPorMetodoPago(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Conteo de clientes en cualquier rango de tiempo
    @Query("""
        SELECT COUNT(h) 
        FROM HistorialCliente h 
        WHERE h.accion = com.owo.TP_prg3.Clases.Persona.historial.Acciones.INSERT
        AND h.fechaEvento BETWEEN :inicio AND :fin """)
    Long countNuevosClientesHistorial(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("""
        SELECT FUNCTION('MONTHNAME', p.transaccion.fecha), 
            SUM(CASE WHEN p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA THEN p.transaccion.monto ELSE 0 END),
            SUM(CASE WHEN p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA THEN p.transaccion.monto ELSE -p.transaccion.monto END)
        FROM Pedido p
        WHERE p.transaccion.fecha BETWEEN :inicio AND :fin
        GROUP BY FUNCTION('MONTHNAME', p.transaccion.fecha) """)
    List<Object[]> getTendenciaMensual(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("""
        SELECT FUNCTION('DATE', p.transaccion.fecha), 
            SUM(CASE WHEN p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA THEN p.transaccion.monto ELSE 0 END),
            SUM(CASE WHEN p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA THEN p.transaccion.monto ELSE -p.transaccion.monto END)
        FROM Pedido p
        WHERE p.transaccion.fecha BETWEEN :inicio AND :fin
        GROUP BY FUNCTION('DATE', p.transaccion.fecha)
        ORDER BY FUNCTION('DATE', p.transaccion.fecha) ASC """)
    List<Object[]> getTendenciaDiaria(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("""
        SELECT CONCAT(FUNCTION('HOUR', p.transaccion.fecha), ':00'), 
            SUM(CASE WHEN p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA THEN p.transaccion.monto ELSE 0 END),
            SUM(CASE WHEN p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA THEN p.transaccion.monto ELSE -p.transaccion.monto END)
        FROM Pedido p
        WHERE p.transaccion.fecha BETWEEN :inicio AND :fin
        GROUP BY CONCAT(FUNCTION('HOUR', p.transaccion.fecha), ':00'), FUNCTION('HOUR', p.transaccion.fecha)
        ORDER BY FUNCTION('HOUR', p.transaccion.fecha) ASC """)
    List<Object[]> getTendenciaPorHora(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // ----- QUERYS PARA LOS KPIs AVANZADOS -----

    // Top 5 productos más vendidos (Casteamos el SUM a Long de manera segura)
    @Query("""
        SELECT d.producto.nombre, SUM(d.cantidad) 
        FROM Pedido p JOIN p.detalles d
        WHERE p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA
        AND p.transaccion.fecha BETWEEN :inicio AND :fin
        GROUP BY d.producto.nombre
        ORDER BY SUM(d.cantidad) DESC """)
    List<Object[]> getTopProductosEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Producto menos vendido
    @Query("""
        SELECT d.producto.nombre, SUM(d.cantidad) 
        FROM Pedido p JOIN p.detalles d
        WHERE p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA
        AND p.transaccion.fecha BETWEEN :inicio AND :fin
        GROUP BY d.producto.nombre
        ORDER BY SUM(d.cantidad) ASC """)
    List<Object[]> getProductoMenosVendidoEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Categoría que MÁS VENDE
    @Query("""
        SELECT d.producto.categoria, SUM(p.transaccion.monto)
        FROM Pedido p JOIN p.detalles d
        WHERE p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA
        AND p.transaccion.fecha BETWEEN :inicio AND :fin
        GROUP BY d.producto.categoria
        ORDER BY SUM(p.transaccion.monto) DESC """)
    List<Object[]> getCategoriaTopEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // ----- QUERYS PARA LA CAJA -----

    // Sumar ventas que fueron hechas estrictamente en EFECTIVO en un rango 
    @Query("""
        SELECT SUM(p.transaccion.monto) 
        FROM Pedido p
        WHERE p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.VENTA 
        AND p.transaccion.tipo = 'EFECTIVO'
        AND p.transaccion.fecha BETWEEN :inicio AND :fin """)
    BigDecimal sumVentasEfectivoEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Sumar los gastos/compras hechos en un rango 
    @Query("""
        SELECT SUM(p.transaccion.monto) 
        FROM Pedido p
        WHERE p.tipo = com.owo.TP_prg3.Clases.Enum.TipoPedido.COMPRA 
        AND p.transaccion.fecha BETWEEN :inicio AND :fin """)
    BigDecimal sumGastosEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
