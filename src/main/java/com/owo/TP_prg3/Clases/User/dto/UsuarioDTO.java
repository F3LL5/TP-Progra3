package com.owo.TP_prg3.Clases.User.dto;

import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.User.modelo.RolUsuario;
import com.owo.TP_prg3.Front.Utilidades.Column;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    @Column(order = 0, name = "ID Usuario")
    private Long id;

    @Column(order = 1, name = "Rol")
    private RolUsuario rol;

    @Column(order = 2, name = "ID Entidad")
    private Long entidadId;

    @Column(order = 3, name = "Dni")
    private Integer dni;

}
