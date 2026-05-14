package com.gymapp.ms_suscripciones.service;

import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;

public interface SuscripcionService {
    SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO request);
    SuscripcionResponseDTO obtenerSuscripcionPorMiembro(Long miembroId);
}