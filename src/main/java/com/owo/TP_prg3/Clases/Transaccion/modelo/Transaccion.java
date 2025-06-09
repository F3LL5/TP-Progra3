package com.owo.TP_prg3.Clases.Transaccion.modelo;

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
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Data @AllArgsConstructor @NoArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaccion_id")
    protected Long transaccionId;

    @Column(nullable = false, length = 100)
    protected String tipo;

    @Column(nullable = false)
    protected LocalDateTime fecha; // Mapeado a TIMESTAMP en DB, default current_timestamp

    @Column(nullable = false, precision = 10, scale = 2)
    protected BigDecimal monto;

    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id", referencedColumnName = "cuenta_bancaria_id")
    protected CuentaBancaria cuentaOrigen;

    @ManyToOne
    @JoinColumn(name = "cuenta_destino_id", referencedColumnName = "cuenta_bancaria_id")
    protected CuentaBancaria cuentaDestino;
}
