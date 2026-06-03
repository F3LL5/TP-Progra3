package com.owo.TP_prg3.Clases.CuentaBancaria.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import com.owo.TP_prg3.Clases.Interfaces.MetodoDePago;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;


@Entity
@Table(name = "cuenta_bancarias")
@Data @AllArgsConstructor @NoArgsConstructor
public class CuentaBancaria implements MetodoDePago {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_bancaria_id")
    private Long cuentaBancariaId;

    
    @Column(name= "nombre_banco", unique = false, nullable = false)
    protected String nombreBanco;

    @Column(name = "cbu", nullable = false, unique = true)
    private Integer cbu;

    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo;

    @EqualsAndHashCode.Exclude @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tienda_id", nullable = false)
    private Tienda tienda;

     @Override
    public Long obtenerId() {
        return 0L; 
    }


    @Override
    public BigDecimal obtenerSaldo() {
        return this.saldo;
    }

    @Override
    public void actualizarSaldo(BigDecimal nuevoSaldo) {
        this.saldo = nuevoSaldo;
    }
    
    @Override
    public String obtenerTipo() {
        return "CAJA";
    }
}
