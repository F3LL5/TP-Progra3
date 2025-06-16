package com.owo.TP_prg3.Clases.User.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUsuarioDTO {
    @NotNull
    @Positive(message = "El DNI debe ser un número positivo.")
    private Integer dni;

    @NotBlank
    @Size(max = 8, message = "La contraseña no debe pasar los 8 (OCHO) caracteres.")
    private String password;
}
