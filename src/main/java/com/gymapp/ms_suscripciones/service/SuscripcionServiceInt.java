package com.gymapp.ms_suscripciones.service;

import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;

import java.util.List;
import java.util.Optional;

public interface SuscripcionServiceInt {

    List<SuscripcionResponseDTO> listarTodas();

    Optional<SuscripcionResponseDTO> obtenerPorId(Long id);

    List<SuscripcionResponseDTO> buscarPorMiembro(Long miembroId);

    SuscripcionResponseDTO crear(SuscripcionRequestDTO dto);

    void cancelar(Long id);
}
