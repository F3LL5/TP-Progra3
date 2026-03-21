package com.owo.TP_prg3.Clases.Proveedor.dto;

import com.owo.TP_prg3.Clases.Domicilio.Domicilio;
import com.owo.TP_prg3.Clases.Enum.CondicionIVA;

import lombok.Data;

@Data
public class FormProveedorDTO {
    private Long cuit; 
    private String razonSocial;
    private String nombreFantasia;
    private CondicionIVA condicion; 
    private Domicilio direccion; 
    private Integer telefono; 
    private String email;
}
