package com.owo.TP_prg3.Clases.Tienda.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;

@Entity
@Table(name = "tiendas")
@Data @AllArgsConstructor @NoArgsConstructor
public class Tienda {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tienda_id")
    protected Long puestoId;

    @Column(name = "nombre", nullable = false)
    protected String nombre;

    @Column(name = "direccion", nullable = false)
    protected String direccion;

    /*
    @Column(name = "caja", nullable = false)
    protected Caja caja;
    protected CuentaBancaria ctaBancaria;
    
    */

    @ManyToOne
    @JoinColumn(name = "persona_id", referencedColumnName = "persona_id")
    protected Persona duenio;

    @Column(nullable = false)
    protected BigDecimal comision;

}
