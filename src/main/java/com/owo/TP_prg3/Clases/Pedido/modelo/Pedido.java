package com.owo.TP_prg3.Clases.Pedido.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;

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

    @Column(name = "puesto_id")
    protected Long puestoId;
}
