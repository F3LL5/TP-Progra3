package com.owo.TP_prg3.Clases.Persona.Cliente.modelo;

import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "persona_id")
@Data @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class Cliente extends Persona {

}
