package com.owo.TP_prg3.Clases.Pedido.modelo;

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
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;

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

    @ManyToOne
    @JoinColumn(name = "detalle_pedido_id", referencedColumnName = "detalle_pedido_id")
    protected DetallePedido detallePedido;
}
