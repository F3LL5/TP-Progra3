package com.owo.TP_prg3.Clases.Proveedor.dto;

import com.owo.TP_prg3.Clases.Domicilio.Domicilio;
import com.owo.TP_prg3.Clases.Enum.CondicionIVA;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorDTO {
    private Long proveedorId;
    private Long cuit;
    private String razonSocial;
    private String nombreFantasia;
    private CondicionIVA condicion;
    private Domicilio direccion;
    private Integer telefono;
    private String email;
}
