package com.owo.TP_prg3.Clases.Transaccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {

    protected Long transaccionId;

    protected String tipo;

    protected String fecha;

    protected BigDecimal monto;

    protected Long cuentaOrigenId;

    protected Long cuentaDestinoId;
}
