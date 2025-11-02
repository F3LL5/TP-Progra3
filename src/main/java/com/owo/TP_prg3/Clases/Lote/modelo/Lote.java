package com.owo.TP_prg3.Clases.Lote.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "lotes")
@Data @AllArgsConstructor @NoArgsConstructor
public class Lote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lote_id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidadDisponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "costoUnitario", nullable = false)
    private BigDecimal costoUnitario;

    @Column(name = "fechaIngreso")
    private LocalDate fechaIngreso;

}
