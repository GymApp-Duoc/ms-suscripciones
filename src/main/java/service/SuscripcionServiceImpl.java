package com.gymapp.ms_suscripciones.service;

import com.gymapp.ms_suscripciones.service.MiembroClient;
import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.exception.BusinessException;
import com.gymapp.ms_suscripciones.exception.ResourceNotFoundException;
import com.gymapp.ms_suscripciones.model.Plan;
import com.gymapp.ms_suscripciones.model.Suscripcion;
import com.gymapp.ms_suscripciones.repository.PlanRepository;
import com.gymapp.ms_suscripciones.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuscripcionServiceImpl implements com.gymapp.ms_suscripciones.service.SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final MiembroClient miembroClient;

    @Override
    @Transactional
    public SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO request) {
        // 1. Validar si ya tiene una suscripción activa
        Optional<Suscripcion> suscripcionActiva = suscripcionRepository
                .findByMiembroIdAndEstado(request.miembroId(), "ACTIVA");

        if (suscripcionActiva.isPresent()) {
            throw new BusinessException("El miembro ya posee una suscripción ACTIVA.");
        }

        // 2. Verificar en ms-miembros si el usuario existe
        try {
            boolean existe = miembroClient.verificarMiembroExiste(request.miembroId());
            if (!existe) {
                throw new ResourceNotFoundException("El miembro con ID " + request.miembroId() + " no existe.");
            }
        } catch (Exception e) {
            throw new BusinessException("Error al conectar con ms-miembros: " + e.getMessage());
        }

        // 3. Buscar el plan
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new ResourceNotFoundException("El plan solicitado no existe."));

        // 4. Crear entidad
        Suscripcion nuevaSuscripcion = Suscripcion.builder()
                .miembroId(request.miembroId())
                .plan(plan)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(plan.getDuracionMeses()))
                .estado("ACTIVA")
                .build();

        Suscripcion guardada = suscripcionRepository.save(nuevaSuscripcion);

        return mapearAResponse(guardada);
    }

    @Override
    public SuscripcionResponseDTO obtenerSuscripcionPorMiembro(Long miembroId) {
        Suscripcion suscripcion = suscripcionRepository.findByMiembroIdAndEstado(miembroId, "ACTIVA")
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró suscripción activa para este miembro."));
        return mapearAResponse(suscripcion);
    }

    // Método helper para no exponer la entidad
    private SuscripcionResponseDTO mapearAResponse(Suscripcion suscripcion) {
        return new SuscripcionResponseDTO(
                suscripcion.getId(),
                suscripcion.getMiembroId(),
                suscripcion.getPlan().getNombre(),
                suscripcion.getFechaInicio(),
                suscripcion.getFechaFin(),
                suscripcion.getEstado()
        );
    }
}

