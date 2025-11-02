package com.owo.TP_prg3.Clases.Producto.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity @Table(name = "productos")
@Data @AllArgsConstructor
public class Producto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id", unique = true)
    protected Long producto_id;

    @Column(name= "nombre", unique = true, nullable = false)
    protected String nombre;

    @Column(name= "categoria", nullable = false)
    protected String categoria;

}
