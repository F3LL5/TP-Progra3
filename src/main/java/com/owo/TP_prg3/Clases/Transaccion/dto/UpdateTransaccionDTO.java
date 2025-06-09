package com.owo.TP_prg3.Clases.Transaccion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransaccionDTO {
    @Size(max = 100)
    protected String tipo;

    protected LocalDateTime fecha;

    @Min(value = 0)
    protected BigDecimal monto;

    protected Long cuentaOrigenId;

    protected Long cuentaDestinoId;
}
