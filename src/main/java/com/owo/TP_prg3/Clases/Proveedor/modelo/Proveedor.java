package com.owo.TP_prg3.Clases.Proveedor.modelo;

import com.owo.TP_prg3.Clases.Persona.modelo.Persona;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedores")
@Data @NoArgsConstructor @AllArgsConstructor
public class Proveedor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proveedorId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    // ---------------------------------------------
    // DELEGACIÓN (Patrón para evitar cliente.persona.getNombre())
    // ---------------------------------------------
    
    public String getNombre() {
        return this.persona.getNombre();
    }
    public Integer getEdad() {
        return this.persona.getEdad();
    }
    public Integer getDni() {
        return this.persona.getDni();
    }
    public Long getPersonaId() {
        return this.persona.getPersonaId();
    }
    
    public void setNombre(String nombre) {
        if (this.persona == null) this.persona = new Persona(); // Inicializar si es null
        this.persona.setNombre(nombre);
    }
    public void setEdad(int edad) {
        if (this.persona == null) this.persona = new Persona();
        this.persona.setEdad(edad);
    }
    public void setDni(int dni) {
        if (this.persona == null) this.persona = new Persona();
        this.persona.setDni(dni);
    }
    
}
