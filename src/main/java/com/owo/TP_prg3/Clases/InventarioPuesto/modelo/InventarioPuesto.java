package com.owo.TP_prg3.Clases.InventarioPuesto.modelo;

import com.owo.TP_prg3.Clases.Puesto.modelo.Puesto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "inventario_puesto")
@Data @AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class InventarioPuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventario_id")
    protected Long inventario_id;

    @Column(nullable = false)
    protected Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "puesto_id", referencedColumnName = "puesto_id")
    protected Puesto puesto;

    @Column(name = "item_id", nullable = false)
    protected Long itemId;

    @Column(name = "stock_min", nullable = false)
    protected Integer stockMin;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    protected BigDecimal precioVenta;

    @Column(name = "costo_adquisicion", nullable = false, precision = 10, scale = 2)
    protected BigDecimal costoAdquisicion;

}
