package com.owo.TP_prg3.Clases.Lote.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.owo.TP_prg3.Clases.Producto.dto.FormProductoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class FormLoteDTO {

    private FormProductoDTO producto;

    private Integer cantidadDisponible;

    private BigDecimal costoUnitario;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaIngreso;
}
