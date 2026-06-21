package com.gymapp.ms_suscripciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia para el envío de alertas mediante el microservicio de notificaciones")
public class NotificacionRequestDTO {

    @Schema(description = "ID del miembro destinatario de la alerta", example = "1")
    private Long miembroId;

    @Schema(description = "Título principal de la notificación", example = "Suscripción Activada")
    private String titulo;

    @Schema(description = "Cuerpo o detalle del mensaje", example = "¡Éxito! Tu plan ANUAL está activo hasta el 2027-06-21")
    private String mensaje;
}