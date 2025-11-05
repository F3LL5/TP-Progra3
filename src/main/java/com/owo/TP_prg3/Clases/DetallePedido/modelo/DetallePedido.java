package com.owo.TP_prg3.Clases.DetallePedido.modelo;

import java.math.BigDecimal;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "detalles_pedido")
@Data @AllArgsConstructor @NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_pedido_id")
    protected Long detallePedidoId;

    @ManyToOne 
    @JoinColumn(name = "pedido_id", nullable = false) 
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Pedido pedido; 

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    protected Producto producto;

    @Column(name = "cantidad", nullable = false)
    protected Integer cantidad;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    protected BigDecimal subtotal;

    public DetallePedido(Producto producto, Integer cantidad, BigDecimal subtotal) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }
}
