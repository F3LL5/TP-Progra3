package com.owo.TP_prg3.Clases.Transaccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.owo.TP_prg3.Clases.Enum.TipoTransaccion;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransaccionDTO {

    private Long transaccion_id;
    private TipoTransaccion tipo;
    private LocalDateTime fecha;
    private BigDecimal monto;
    private Long origen_id; 
    private Long destino_id; 
    private String motivo;
}
