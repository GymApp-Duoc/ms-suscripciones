package com.gymapp.ms_suscripciones.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionRequestDTO {

    @NotNull(message = "El ID del miembro es obligatorio")
    @Positive(message = "El ID del miembro debe ser un número positivo")
    private Long miembroId;

    @NotBlank(message = "El tipo de plan es obligatorio")
    @Pattern(regexp = "^(MENSUAL|TRIMESTRAL|ANUAL)$", message = "El plan debe ser MENSUAL, TRIMESTRAL o ANUAL")
    private String tipoPlan;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;
}