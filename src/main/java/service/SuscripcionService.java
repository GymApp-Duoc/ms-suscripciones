package service;

import dto.SuscripcionRequestDTO;
import dto.SuscripcionResponseDTO;

public interface SuscripcionService {

    SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO request);

    SuscripcionResponseDTO obtenerSuscripcionPorMiembro(Long miembroId);

    // Estos son los dos métodos que Java te está pidiendo a gritos:
    void cancelarSuscripcion(Long id);

    void actualizarEstado(Long id, String estado);
}