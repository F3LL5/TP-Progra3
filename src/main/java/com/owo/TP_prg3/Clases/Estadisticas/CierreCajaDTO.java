package com.owo.TP_prg3.Clases.Estadisticas;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CierreCajaDTO {
    private BigDecimal ingresosEfectivo;
    private BigDecimal egresosGastos;
    private BigDecimal ajustesHoy;
}
