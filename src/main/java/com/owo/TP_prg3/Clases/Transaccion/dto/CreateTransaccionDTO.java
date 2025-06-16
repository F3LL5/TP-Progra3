package com.owo.TP_prg3.Clases.Transaccion.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransaccionDTO {
    @NotBlank
    @Size(max = 100, message = "El tipo no debe pasar los 100 caracteres.")
    protected String tipo;

    @NotNull
    @Positive(message = "El costo debe ser un número positivo.")
    protected BigDecimal monto;

    @NotNull
    @Positive(message = "El ID de la cuenta origen debe ser un número positivo.")
    protected Long cuentaOrigenId;

    @NotNull
    @Positive(message = "El ID de la cuenta destino debe ser un número positivo.")
    protected Long cuentaDestinoId;
}
