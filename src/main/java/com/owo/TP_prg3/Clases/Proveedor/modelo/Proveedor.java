package com.owo.TP_prg3.Clases.Proveedor.modelo;

import com.owo.TP_prg3.Clases.Domicilio.Domicilio;
import com.owo.TP_prg3.Clases.Enum.CondicionIVA;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedores")
@Data @NoArgsConstructor @AllArgsConstructor
public class Proveedor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proveedorId;

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

    @Column(name = "telefono", length = 20)
    private Integer telefono;

    @Column(name = "email", length = 100)
    private String email;

}
