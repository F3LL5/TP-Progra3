package com.owo.TP_prg3.Clases.Tienda.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

import com.owo.TP_prg3.Clases.Caja.Caja;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;

@Entity
@Table(name = "tiendas")
@Data @AllArgsConstructor
public class Tienda {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tienda_id")
    protected Long tiendaId;

    @Column(name = "nombre", nullable = false)
    protected String nombre;

    @Column(name = "direccion", nullable = false)
    protected String direccion;

    @Embedded
    private Caja caja;

    @EqualsAndHashCode.Exclude @ToString.Exclude
    @OneToMany(mappedBy = "tienda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected Set<CuentaBancaria> cuentaBancaria;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "duenio_id")
    protected Duenio duenio;

    public Tienda() {
        this.caja = new Caja();
        this.cuentaBancaria = new HashSet<>();
    }
}
