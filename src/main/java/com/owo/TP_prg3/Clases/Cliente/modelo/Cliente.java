package com.owo.TP_prg3.Clases.Cliente.modelo;

import java.time.LocalDate;

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
@Table(name = "clientes")
@Data @NoArgsConstructor @AllArgsConstructor
public class Cliente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    // ---------------------------------------------
    // DELEGACIÓN (Patrón para evitar cliente.persona.getNombre())
    // ---------------------------------------------
    
    public String getNombre() {
        return this.persona.getNombre();
    }

    public String getApellido() {
        return this.persona.getApellido();
    }

    public Long getDni() {
        return this.persona.getDni();
    }

    public Long getPersonaId() {
        return this.persona.getPersonaId();
    }

    public LocalDate getFechaNacimiento() {
        return this.persona.getFechaNacimiento();
    }
    
    public void setNombre(String nombre) {
        this.persona.setNombre(nombre);
    }

    public void setApellido(String apellido) {
        this.persona.setNombre(apellido);
    }

    public void setDni(Long dni) {
        this.persona.setDni(dni);
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.persona.setFechaNacimiento(fechaNacimiento);
    }
    
}
