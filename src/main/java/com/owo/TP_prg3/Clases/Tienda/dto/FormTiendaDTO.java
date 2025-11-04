package com.owo.TP_prg3.Clases.Tienda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormTiendaDTO {

    protected String nombre;

    protected String direccion;

    protected BigDecimal caja;

    protected Integer duenioDni;

    protected Integer cbu;

}
