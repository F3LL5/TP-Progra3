package com.owo.TP_prg3.Clases.Transaccion.dto;

import com.owo.TP_prg3.Front.Utilidades.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    @Column(order = 0, name = "ID")
    protected Long transaccionId;

    @Column(order = 1, name = "TIPO")
    protected String tipo;

    @Column(order = 2, name = "FECHA")
    protected String fecha;

    @Column(order = 3, name = "MONTO")
    protected BigDecimal monto;

    @Column(order = 4, name = "CUENTA ORIGEN")
    protected Long cuentaOrigenId;

    @Column(order = 5, name = "CUENA DESTINO")
    protected Long cuentaDestinoId;
}
