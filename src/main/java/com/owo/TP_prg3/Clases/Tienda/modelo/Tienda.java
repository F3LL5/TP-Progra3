package com.owo.TP_prg3.Clases.Tienda.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.owo.TP_prg3.Clases.Caja.Caja;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.Domicilio.Domicilio;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Enum.CondicionIVA;

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
