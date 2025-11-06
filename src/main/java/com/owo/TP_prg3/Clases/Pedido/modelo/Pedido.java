package com.owo.TP_prg3.Clases.Pedido.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.owo.TP_prg3.Clases.DetallePedido.modelo.DetallePedido;
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
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // ID de la Persona o Tienda que remite el pedido.
    @Column(name = "remitente_id", nullable = false)
    private Long remitenteId; 

    // ID de la Persona, Tienda o Proveedor que recibe el pedido. 
    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId; 
    
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

