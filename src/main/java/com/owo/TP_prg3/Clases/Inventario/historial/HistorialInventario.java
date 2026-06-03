package com.owo.TP_prg3.Clases.Inventario.historial;

import com.owo.TP_prg3.Clases.Persona.historial.Acciones;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_inventarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historial_inventario_id", unique = true)
    private Long historialInventarioId;

    @Column(name = "inventario_id")
    private Long inventarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Acciones accion;

    @Column(name = "fecha_evento", insertable = false, updatable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "campo_modificado")
    private String campoModificado;

    @Column(name = "valor_anterior")
    private String valorAnterior;

    @Column(name = "valor_nuevo")
    private String valorNuevo;
}
