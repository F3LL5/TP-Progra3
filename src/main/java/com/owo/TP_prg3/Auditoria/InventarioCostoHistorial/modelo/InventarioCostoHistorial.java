package com.owo.TP_prg3.Auditoria.InventarioCostoHistorial.modelo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity @Table(name="inventario_costo_historial")
@Data @AllArgsConstructor @NoArgsConstructor

public class InventarioCostoHistorial {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id",unique = true)
    protected Long id_historial_item;

    @Column (nullable = false)
    protected Long item_id;

    @Column (nullable = false)
    protected Double costo_anterior;

    @Column (nullable = false)
    protected Double costo_nuevo;

    @Column (nullable = false)
    protected LocalDateTime fecha_cambio;
}
