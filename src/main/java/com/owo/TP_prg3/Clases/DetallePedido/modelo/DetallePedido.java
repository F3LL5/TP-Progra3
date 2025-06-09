package com.owo.TP_prg3.Clases.DetallePedido.modelo;

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
import com.owo.TP_prg3.Clases.Item.modelo.Item;

@Entity
@Table(name = "detalles_pedido")
@Data @AllArgsConstructor @NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_pedido_id")
    protected Long detallePedidoId;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    protected Item item;

    @Column(nullable = false)
    protected Integer cantidad;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    protected java.math.BigDecimal precioTotal;
}
