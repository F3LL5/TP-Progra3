package com.owo.TP_prg3.Clases.CuentaBancaria.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;


@Entity
@Table(name = "cuenta_bancarias")
@Data @AllArgsConstructor @NoArgsConstructor
public class CuentaBancaria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_bancaria_id")
    private Long cuentaBancariaId;

    @Column(name = "cbu", nullable = false, unique = true)
    private Integer cbu;

    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tienda_id", nullable = false)
    private Tienda tienda;
}
