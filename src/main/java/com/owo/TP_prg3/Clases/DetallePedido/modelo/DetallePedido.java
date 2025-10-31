package com.owo.TP_prg3.Clases.DetallePedido.modelo;

import com.owo.TP_prg3.Clases.Pedido.modelo.Pedido;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_pedido")
@Data @AllArgsConstructor @NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_pedido_id")
    protected Long detallePedidoId;

    // Muchos detalles de pedido pueden pertenecer a un solo pedido
    @ManyToOne
    @JoinColumn(name = "pedido_id", referencedColumnName = "pedido_id", nullable = false)
    protected Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id", referencedColumnName = "producto_id", nullable = false)
    protected Producto producto;

    @Column(nullable = false)
    protected Integer cantidad;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    protected java.math.BigDecimal precioTotal;
}
