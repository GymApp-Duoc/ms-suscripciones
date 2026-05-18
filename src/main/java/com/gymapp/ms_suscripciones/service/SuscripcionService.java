package com.gymapp.ms_suscripciones.service;

import com.gymapp.ms_suscripciones.client.MiembroClient;
import com.gymapp.ms_suscripciones.client.NotificacionClient;
import com.gymapp.ms_suscripciones.dto.NotificacionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.exception.BusinessException;
import com.gymapp.ms_suscripciones.exception.RecursoNoEncontradoException;
import com.gymapp.ms_suscripciones.model.Suscripcion;
import com.gymapp.ms_suscripciones.repository.SuscripcionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuscripcionService implements SuscripcionServiceInt {

    private final SuscripcionRepository repository;
    private final MiembroClient miembroClient;
    private final NotificacionClient notificacionClient;

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarTodas() {
        log.info("Consultando todas las suscripciones");
        return repository.findAll().stream().map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SuscripcionResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(this::mapearADto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> buscarPorMiembro(Long miembroId) {
        validarMiembroExterno(miembroId);
        return repository.findByMiembroId(miembroId).stream().map(this::mapearADto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SuscripcionResponseDTO crear(SuscripcionRequestDTO dto) {
        validarMiembroExterno(dto.getMiembroId());


        if (repository.existsByMiembroIdAndEstado(dto.getMiembroId(), "ACTIVA")) {
            throw new BusinessException("El miembro ya posee una suscripción ACTIVA.");
        }

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = calcularFechaFin(fechaInicio, dto.getTipoPlan());

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setMiembroId(dto.getMiembroId());
        suscripcion.setTipoPlan(dto.getTipoPlan());
        suscripcion.setFechaInicio(fechaInicio);
        suscripcion.setFechaFin(fechaFin);
        suscripcion.setEstado("ACTIVA");
        suscripcion.setPrecio(dto.getPrecio());


        Suscripcion guardada = repository.save(suscripcion);


        try {
            NotificacionRequestDTO notificacion = NotificacionRequestDTO.builder()
                    .miembroId(guardada.getMiembroId())
                    .titulo("Suscripción Activada")
                    .mensaje("¡Éxito! Tu plan " + guardada.getTipoPlan() + " está activo hasta el " + guardada.getFechaFin())
                    .build();
            notificacionClient.enviarNotificacion(notificacion);
            log.info("Notificación de éxito enviada al miembro ID: {}", guardada.getMiembroId());
        } catch (Exception e) {
            log.error("La suscripción se creó, pero falló el envío de la notificación: {}", e.getMessage());
        }

        return mapearADto(guardada);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        repository.findById(id).ifPresent(suscripcion -> {
            if ("CANCELADA".equals(suscripcion.getEstado())) {
                throw new BusinessException("Esta suscripción ya está cancelada.");
            }
            suscripcion.setEstado("CANCELADA");
            repository.save(suscripcion);
            log.info("Suscripción cancelada: {}", id);
        });
    }

    private void validarMiembroExterno(Long id) {
        try {
            miembroClient.obtenerPorId(id);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Miembro ID " + id + " no encontrado.");
        } catch (FeignException e) {
            log.error("Error comunicándose con el servicio de miembros", e);
            throw new BusinessException("Servicio de miembros no disponible en este momento.");
        }
    }

    private LocalDate calcularFechaFin(LocalDate inicio, String tipoPlan) {
        return switch (tipoPlan.toUpperCase()) {
            case "MENSUAL" -> inicio.plusMonths(1);
            case "TRIMESTRAL" -> inicio.plusMonths(3);
            case "ANUAL" -> inicio.plusYears(1);
            default -> throw new BusinessException("Tipo de plan desconocido.");
        };
    }

    private SuscripcionResponseDTO mapearADto(Suscripcion suscripcion) {
        return SuscripcionResponseDTO.builder()
                .id(suscripcion.getId())
                .miembroId(suscripcion.getMiembroId())
                .tipoPlan(suscripcion.getTipoPlan())
                .fechaInicio(suscripcion.getFechaInicio())
                .fechaFin(suscripcion.getFechaFin())
                .estado(suscripcion.getEstado())
                .precio(suscripcion.getPrecio())
                .build();
    }
}