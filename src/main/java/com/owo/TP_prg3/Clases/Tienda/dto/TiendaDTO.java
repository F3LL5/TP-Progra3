package com.owo.TP_prg3.Clases.Tienda.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.owo.TP_prg3.Clases.Domicilio.Domicilio;
import com.owo.TP_prg3.Clases.Enum.CondicionIVA;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiendaDTO {

    protected Long tiendaId;

    protected Long cuit;

    protected String RazonSocial;

    protected String nombreFantasia;

    protected CondicionIVA condicion;

    protected String url;

    protected Domicilio direccion;

    protected String ingresosBrutos;

    protected LocalDate fechaInicioActividades;

    protected Long puntoDeVenta;

    protected BigDecimal caja;

    protected Long duenioDni;

}
