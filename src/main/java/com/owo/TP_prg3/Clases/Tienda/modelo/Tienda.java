package com.owo.TP_prg3.Clases.Tienda.modelo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.owo.TP_prg3.Clases.Caja.Caja;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.Domicilio.Domicilio;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Enum.CondicionIVA;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "tiendas")
@Data @AllArgsConstructor
public class Tienda {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tienda_id")
    protected Long tiendaId;

    @Column(name = "cuit", nullable = false, unique = true, length = 13)
    private Long cuit;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "nombre_fantasia", length = 150)
    private String nombreFantasia;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicion_iva", nullable = false)
    private CondicionIVA condicion;

    @Column(name= "tienda_imagen", nullable = true)
    protected String url;

    @Embedded
    private Domicilio direccion;

    @Column(name = "ingresos_brutos")
    private String ingresosBrutos;

    @Column(name = "fecha_inicio_actividades")
    private LocalDate fechaInicioActividades;

    @Column(name = "punto_de_venta", nullable = false)
    private Long puntoDeVenta;

    @Embedded
    private Caja caja;

    @Column(name = "ultimo_cierre_caja")
    private LocalDate ultimoCierreCaja;

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
