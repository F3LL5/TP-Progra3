package com.owo.TP_prg3.Clases.ItemPrecioHistorial.modelo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity @Table(name="item_precio_historial")
@Data @AllArgsConstructor @NoArgsConstructor


public class ItemPrecioHistorial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id",unique = true)
    protected Long id_historial_item;

    @Column (nullable = false)
    protected Long item_id;

    @Column (nullable = false)
    protected Double precio_anterior;

    @Column (nullable = false)
    protected Double precio_nuevo;

    @Column (nullable = false)
    protected LocalDate fecha_cambio;
}
