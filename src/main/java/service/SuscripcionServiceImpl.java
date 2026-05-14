package service;

import dto.SuscripcionRequestDTO;
import dto.SuscripcionResponseDTO;
import model.Plan;
import model.Suscripcion;
import repository.PlanRepository;
import repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SuscripcionServiceImpl implements SuscripcionService {

    private final PlanRepository planRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final RestTemplate restTemplate;

    @Value("${ms.miembros.url}")
    private String miembrosUrl;

    // Si ms-suscripciones no tiene esta variable en el application.properties, usa el localhost por defecto
    @Value("${ms.gamificacion.url:http://localhost:8089}")
    private String gamificacionUrl;

    @Override
    @Transactional
    public SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO dto) {

        // 1. Validar que el plan exista
        Plan plan = planRepository.findById(dto.planId())
                .orElseThrow(() -> new RuntimeException("Plan no disponible o inexistente"));

        // 2. Validar que el miembro no tenga ya una suscripción activa
        suscripcionRepository.findByMiembroIdAndEstado(dto.miembroId(), "ACTIVA")
                .ifPresent(s -> {
                    throw new RuntimeException("El miembro ya posee una suscripción ACTIVA");
                });

        // 3. Validar con ms-miembros usando RestTemplate
        validarMiembroExterno(dto.miembroId());

        // 4. Crear la entidad Suscripcion
        Suscripcion suscripcion = Suscripcion.builder()
                .miembroId(dto.miembroId())
                .plan(plan)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(plan.getDuracionMeses()))
                .estado("ACTIVA")
                .build();

        Suscripcion guardada = suscripcionRepository.save(suscripcion);

        // 5. Enviar puntos a Gamificación (Ej: 100 puntos por adquirir un plan)
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("miembroId", dto.miembroId());
            evento.put("accion", "COMPRA_SUSCRIPCION");
            evento.put("puntosBase", 100);

            restTemplate.postForObject(gamificacionUrl + "/api/gamificacion/eventos", evento, Object.class);
            System.out.println("Evento enviado a Gamificación exitosamente.");
        } catch (Exception e) {
            System.err.println("Aviso: No se pudieron enviar los puntos a Gamificación. " + e.getMessage());
        }

        // 6. Retornar el DTO de respuesta
        return new SuscripcionResponseDTO(
                guardada.getId(),
                guardada.getMiembroId(),
                plan.getNombre(),
                guardada.getFechaInicio(),
                guardada.getFechaFin(),
                guardada.getEstado()
        );
    }

    @Override
    public SuscripcionResponseDTO obtenerSuscripcionPorMiembro(Long miembroId) {
        Suscripcion suscripcion = suscripcionRepository.findByMiembroIdAndEstado(miembroId, "ACTIVA")
                .orElseThrow(() -> new RuntimeException("No se encontró suscripción activa para este miembro."));

        return new SuscripcionResponseDTO(
                suscripcion.getId(),
                suscripcion.getMiembroId(),
                suscripcion.getPlan().getNombre(),
                suscripcion.getFechaInicio(),
                suscripcion.getFechaFin(),
                suscripcion.getEstado()
        );
    }

    @Override
    @Transactional
    public void cancelarSuscripcion(Long id) {
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        suscripcion.setEstado("CANCELADA");
        suscripcionRepository.save(suscripcion);
    }

    @Override
    @Transactional
    public void actualizarEstado(Long id, String estado) {
        Suscripcion suscripcion = suscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        suscripcion.setEstado(estado);
        suscripcionRepository.save(suscripcion);
    }

    // Método privado idéntico al de TiendaServiceImpl
    private void validarMiembroExterno(Long id) {
        try {
            Boolean ok = restTemplate.getForObject(miembrosUrl + "/api/miembros/validar/" + id, Boolean.class);
            if (ok == null || !ok) throw new RuntimeException("Miembro no autorizado o no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con MS-MIEMBROS: " + e.getMessage());
        }
    }
}