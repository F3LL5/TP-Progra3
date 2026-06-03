package com.owo.TP_prg3.Clases.Lote.historial;

import com.owo.TP_prg3.Clases.Persona.historial.Acciones;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_lotes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historial_lote_id", unique = true)
    private Long historialLoteId;

    @Column(name = "lote_id")
    private Long loteId;

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
