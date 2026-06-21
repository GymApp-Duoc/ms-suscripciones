package com.gymapp.ms_suscripciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto consolidado que representa el estado de una suscripción activa o cancelada")
public class SuscripcionResponseDTO {
    private Long id;
    private Long miembroId;
    private String tipoPlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Double precio;
}