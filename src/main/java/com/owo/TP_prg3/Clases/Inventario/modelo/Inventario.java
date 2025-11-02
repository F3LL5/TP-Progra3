package com.owo.TP_prg3.Clases.Inventario.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;

@Entity
@Table(name = "inventarios")
@Data @AllArgsConstructor @NoArgsConstructor @DynamicUpdate
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventario_id")
    protected Long inventario_id;

    @Column(nullable = false, updatable = false)
    protected Integer cantidad;

    @Column(name = "producto_id", nullable = false, updatable = false)
    protected Long productoId;

    @Column(name = "stock_min")
    protected Integer stockMin;

    @Column(name = "precio_venta", precision = 10, scale = 2)
    protected BigDecimal precioVenta;

    @Column(name = "costo_adquisicion", nullable = false, precision = 10, scale = 2)
    protected BigDecimal costoAdquisicion;

}
