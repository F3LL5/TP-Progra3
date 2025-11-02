package com.owo.TP_prg3.Clases.CuentaBancaria.modelo;

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
@Table(name = "cuenta_bancaria")
@Data @AllArgsConstructor @NoArgsConstructor
public class CuentaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_bancaria_id")
    protected Long cuentaBancariaId;

    @ManyToOne
    @JoinColumn(name = "persona_id", referencedColumnName = "persona_id")
    protected Persona persona;

    @Column(nullable = false, precision = 10, scale = 2)
    protected BigDecimal saldo;
}
