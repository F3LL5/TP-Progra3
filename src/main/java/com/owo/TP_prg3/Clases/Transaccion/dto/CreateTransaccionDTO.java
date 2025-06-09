package com.owo.TP_prg3.Clases.Transaccion.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransaccionDTO {
    @NotBlank
    @Size(max = 100)
    protected String tipo;

    @NotNull
    @Min(value = 0)
    protected BigDecimal monto;

    protected Long cuentaOrigenId;

    protected Long cuentaDestinoId;
}
