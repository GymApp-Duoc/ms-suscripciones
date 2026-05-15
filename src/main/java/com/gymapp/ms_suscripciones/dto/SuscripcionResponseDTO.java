package com.gymapp.ms_suscripciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionResponseDTO {
    private Long id;
    private Long miembroId;
    private String tipoPlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Double precio;
}