package com.owo.TP_prg3.Clases.Lote.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.owo.TP_prg3.Clases.Producto.modelo.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class LoteDTO {

    private Long lote_id;

    private Producto producto;

    private int cantidadDisponible;

    private BigDecimal costoUnitario;

    private LocalDate fechaIngreso;

}
