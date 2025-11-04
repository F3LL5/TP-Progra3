package com.owo.TP_prg3.Clases.Tienda.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiendaDTO {

    protected Long tiendaId;

    protected String nombre;

    protected String direccion;

    protected BigDecimal caja;

    protected String duenio;

}
