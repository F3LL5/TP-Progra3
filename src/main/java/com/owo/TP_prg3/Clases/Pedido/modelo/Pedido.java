package com.owo.TP_prg3.Clases.Pedido.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;

import java.util.Set;

@Entity
@Table(name = "pedidos")
@Data @AllArgsConstructor @NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    protected Long pedidoId;

    @ManyToOne
    @JoinColumn(name = "transaccion_id", referencedColumnName = "transaccion_id")
    protected Transaccion transaccion;

    // Un pedido tiene muchos detalles de pedido
    @OneToMany(mappedBy = "pedido")
    protected Set<DetallePedido> detallesPedido;
}
