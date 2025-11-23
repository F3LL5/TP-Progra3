package com.owo.TP_prg3.Clases.Pedido.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
import com.owo.TP_prg3.Clases.Enum.EstadoPedido;
import com.owo.TP_prg3.Clases.Enum.TipoPedido;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;

@Entity
@Table(name = "pedidos")
@Data @AllArgsConstructor @NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pedidoId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaccion_id", nullable = false)
    private Transaccion transaccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedido tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;   // por defecto PENDIENTE
    
    // CascadeType.ALL asegura que los detalles se guarden/eliminen con el pedido.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<DetallePedido> detalles = new HashSet<>();
    
    public boolean addDetalle(DetallePedido detalle) {
        return detalles.add(detalle);
    }

    public boolean deleteDetalle(DetallePedido detalle) {
        return detalles.remove(detalle);
    }
}

