package com.owo.TP_prg3.Clases.InventarioPuesto.modelo;

import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Entity
@Table(name = "inventario_puesto")
@Data @AllArgsConstructor @NoArgsConstructor @Getter @Setter @DynamicUpdate
public class InventarioPuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventario_id")
    protected Long inventario_id;

    @Column(nullable = false)
    protected Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puesto_id", referencedColumnName = "puesto_id", nullable = false, updatable = false)
    protected Puesto puesto;

    @Column(name = "producto_id", nullable = false, updatable = false)
    protected Long itemId;

    @Column(name = "stock_min", nullable = false)
    protected Integer stockMin;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    protected BigDecimal precioVenta;

    @Column(name = "costo_adquisicion", nullable = false, precision = 10, scale = 2)
    protected BigDecimal costoAdquisicion;

}
