package com.owo.TP_prg3.Clases.Transaccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.owo.TP_prg3.Utilidades.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    @Column(order = 0, name = "ID Transacción")
    protected Long transaccionId;

    @Column(order = 1, name = "Tipo")
    protected String tipo;

    @Column(order = 2, name = "Fecha y Hora")
    protected String fecha;

    @Column(order = 3, name = "Monto")
    protected BigDecimal monto;

    @Column(order = 4, name = "Cuenta Dueño")
    protected Long cuentaOrigenId;

    @Column(order = 5, name = "Cuenta Tercero")
    protected Long cuentaDestinoId;
}
