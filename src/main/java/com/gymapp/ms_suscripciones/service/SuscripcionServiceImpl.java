package com.gymapp.ms_suscripciones.service;

import com.gymapp.ms_suscripciones.assembler.SuscripcionAssembler;
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
public class SuscripcionServiceImpl implements SuscripcionServiceInt {

    private final SuscripcionRepository repository;
    private final MiembroClient miembroClient;
    private final NotificacionClient notificacionClient;
    private final SuscripcionAssembler assembler; // Inyectado para mapeo

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarTodas() {
        return repository.findAll().stream().map(assembler::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SuscripcionResponseDTO> obtenerPorId(Long id) {
        return repository.findById(id).map(assembler::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> buscarPorMiembro(Long miembroId) {
        validarMiembroExterno(miembroId);
        return repository.findByMiembroId(miembroId).stream().map(assembler::toResponseDTO).collect(Collectors.toList());
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

        Suscripcion suscripcion = new Suscripcion(
                null,
                dto.getMiembroId(),
                dto.getTipoPlan(),
                fechaInicio,
                fechaFin,
                "ACTIVA",
                dto.getPrecio()
        );

        Suscripcion guardada = repository.save(suscripcion);

        // Envío asíncrono de notificación
        try {
            NotificacionRequestDTO notificacion = NotificacionRequestDTO.builder()
                    .miembroId(guardada.getMiembroId())
                    .titulo("Suscripción Activada")
                    .mensaje("¡Éxito! Tu plan " + guardada.getTipoPlan() + " está activo hasta el " + guardada.getFechaFin())
                    .build();
            notificacionClient.enviarNotificacion(notificacion);
        } catch (Exception e) {
            log.warn("Suscripción creada, pero falló notificación: {}", e.getMessage());
        }

        return assembler.toResponseDTO(guardada);
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        Suscripcion suscripcion = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Suscripción no encontrada."));

        if ("CANCELADA".equals(suscripcion.getEstado())) {
            throw new BusinessException("Esta suscripción ya se encuentra cancelada.");
        }

        suscripcion.setEstado("CANCELADA");
        repository.save(suscripcion);
        log.info("Suscripción ID {} cancelada exitosamente.", id);
    }



    @Override
    @Transactional(readOnly = true)
    public long contarSuscripcionesActivas() {
        return repository.countByEstado("ACTIVA");
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarPorTipoPlan(String tipoPlan) {
        return repository.findByTipoPlanIgnoreCaseAndEstado(tipoPlan, "ACTIVA").stream()
                .map(assembler::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarProximasAVencer(int dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        return repository.findByEstadoAndFechaFinBetween("ACTIVA", hoy, limite).stream()
                .map(assembler::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularIngresosTotales() {
        return repository.calcularIngresosTotalesActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarSuscripcionesRecientes(int dias) {
        LocalDate fechaCorte = LocalDate.now().minusDays(dias);
        return repository.findByFechaInicioAfterAndEstado(fechaCorte, "ACTIVA").stream()
                .map(assembler::toResponseDTO).collect(Collectors.toList());
    }



    private void validarMiembroExterno(Long id) {
        try {
            miembroClient.obtenerPorId(id);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("Miembro ID " + id + " no encontrado en el sistema base.");
        } catch (FeignException e) {
            throw new BusinessException("Servicio de miembros no disponible (Error 500).");
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
}