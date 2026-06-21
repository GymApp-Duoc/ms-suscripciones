package com.gymapp.ms_suscripciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para contratar o renovar una suscripción en el gimnasio")
public class SuscripcionRequestDTO {

    @NotNull(message = "El ID del miembro es obligatorio")
    @Positive(message = "El ID del miembro debe ser un número positivo")
    @Schema(description = "ID único del miembro contratante", example = "1")
    private Long miembroId;

    @NotBlank(message = "El tipo de plan es obligatorio")
    @Pattern(regexp = "^(MENSUAL|TRIMESTRAL|ANUAL)$", message = "El plan debe ser MENSUAL, TRIMESTRAL o ANUAL")
    @Schema(description = "Modalidad del plan elegido", example = "ANUAL")
    private String tipoPlan;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Valor total a facturar por la suscripción", example = "299990.0")
    private Double precio;
}