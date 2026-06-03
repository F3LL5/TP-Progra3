package com.owo.TP_prg3.Clases.Producto.historial;

import com.owo.TP_prg3.Clases.Persona.historial.Acciones;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historial_producto_id", unique = true)
    private Long historialProductoId;

    @Column(name = "producto_id")
    private Long productoId;

    private String nombre;

    private String categoria;

    @Column(name = "producto_imagen")
    private String productoImagen;

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
