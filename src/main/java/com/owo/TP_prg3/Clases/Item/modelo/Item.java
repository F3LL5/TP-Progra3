package com.owo.TP_prg3.Clases.Item.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

//JPA
@Entity @Table(name = "items")
//Lombok
@Data @AllArgsConstructor @NoArgsConstructor
@DynamicUpdate
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", unique = true)
    protected Long item_id;

    @Column(nullable = false)
    protected String nombre;

    @Column(nullable = false)
    protected String categoria;

    @Column(nullable = false)
    protected Double costo;
}
