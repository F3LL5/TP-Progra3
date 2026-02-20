package com.owo.TP_prg3.Clases.Usuario.historial;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_usuarios")
@Data @AllArgsConstructor @NoArgsConstructor
public class HistorialUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historial_usuario_id")
    private Long historialUsuarioId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Accion accion;

    @Column(name = "fecha_evento", insertable = false, updatable = false)
    private LocalDateTime fechaEvento;

    private String email;

    @Column(name = "contraseña")
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public enum Accion { INSERT, UPDATE, DELETE }
    public enum Rol { ROLE_ADMIN, ROLE_DUENIO, ROLE_EMPLEADO }
}
