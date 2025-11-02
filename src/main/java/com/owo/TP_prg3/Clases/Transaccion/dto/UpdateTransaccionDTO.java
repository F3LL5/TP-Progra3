package com.owo.TP_prg3.Clases.Transaccion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTransaccionDTO {
    @Size(max = 100, message = "El tipo no debe pasar los 100 caracteres.")
    protected String tipo;

    @PastOrPresent(message = "La fecha debe ser hoy o en el pasado")
    protected LocalDateTime fecha;

    @Positive(message = "El costo debe ser un número positivo.")
    protected BigDecimal monto;

    @Positive(message = "El ID de la cuenta origen debe ser un número positivo.")
    protected Long cuentaOrigenId;

    @Positive(message = "El ID de la cuenta destino debe ser un número positivo.")
    protected Long cuentaDestinoId;
}
