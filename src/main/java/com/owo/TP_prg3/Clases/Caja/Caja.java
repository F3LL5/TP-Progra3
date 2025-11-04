package com.owo.TP_prg3.Clases.Caja;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class Caja {

    @Column(name = "caja", nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

}
