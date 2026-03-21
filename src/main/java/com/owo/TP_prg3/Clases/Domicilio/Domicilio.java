package com.owo.TP_prg3.Clases.Domicilio;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Table(name = "domicilios")
@Data @NoArgsConstructor @AllArgsConstructor
public class Domicilio {
    
    @Column(name = "dir_calle")
    private String calle;

    @Column(name = "dir_altura")
    private String altura;

    @Column(name = "dir_piso")
    private String piso;

    @Column(name = "dir_cp")
    private String codigoPostal;
    
    @Column(name = "dir_localidad")
    private String localidad;
    
    @Column(name = "dir_provincia")
    private String provincia;
    
    @Column(name = "dir_pais")
    private String pais;
}
