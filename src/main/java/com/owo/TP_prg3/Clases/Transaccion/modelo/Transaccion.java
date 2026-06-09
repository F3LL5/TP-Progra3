package com.owo.TP_prg3.Clases.Transaccion.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.owo.TP_prg3.Clases.Enum.TipoTransaccion;

@Entity
@Table(name = "transacciones")
@Data @AllArgsConstructor @NoArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaccion_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now(); 

    @Column(nullable = false)
    private BigDecimal monto;

    // ID del MetodoDePago origen. Puede ser NULL si es un ingreso externo (ej. venta en efectivo).
    @Column(name = "origen_id", nullable = true) 
    private Long origen_id; 

    @Column(name = "destino_id", nullable = false)
    private Long destino_id;

    @Column(length = 255)
    private String motivo;
}
