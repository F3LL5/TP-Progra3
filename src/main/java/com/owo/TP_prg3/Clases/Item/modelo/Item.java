package com.owo.TP_prg3.Clases;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//JPA
@Entity @Table(name = "items")
//Lombok
@Data @AllArgsConstructor @NoArgsConstructor

public class Item {
    @Id @Column(name = "item_id", unique = true)
    protected Long item_id;

    @Column(nullable = false)
    protected String nombre;

    @Column(nullable = false)
    protected String categoria;

    @Column(nullable = false)
    protected Double costo;
}
