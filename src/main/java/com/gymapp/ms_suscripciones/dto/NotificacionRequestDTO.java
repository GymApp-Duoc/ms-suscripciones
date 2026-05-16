package com.gymapp.ms_suscripciones.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionRequestDTO {
    private Long miembroId;
    private String titulo;
    private String mensaje;
}