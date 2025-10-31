package com.owo.TP_prg3.Clases.Item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateItemDTO {

    @Size(max = 100, message = "El nombre no debe pasar los 100 caracteres.")
    protected String nombre;

    @Size(max = 100, message = "La categoria no debe pasar los 100 caracteres.")
    protected String categoria;

}
