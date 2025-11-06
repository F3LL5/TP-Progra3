package com.owo.TP_prg3.Clases.Transaccion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.owo.TP_prg3.Clases.Enum.TipoTransaccion;

@Data @NoArgsConstructor @AllArgsConstructor
public class FormTransaccionDTO {
    
    private TipoTransaccion tipo;
    
    // La fecha puede ser opcional, el servicio pondrá LocalDateTime.now() si es null.
    private LocalDateTime fecha; 
    
    // ID del MetodoDePago origen (Cliente/Caja/Cuenta)
    private Long origen_id; 

    // ID del MetodoDePago destino (Tienda/Proveedor/Caja/Cuenta)
    private Long destino_id;
}
