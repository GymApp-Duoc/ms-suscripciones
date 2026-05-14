package controller;

import dto.SuscripcionRequestDTO;
import dto.SuscripcionResponseDTO;
import model.Plan;
import repository.PlanRepository;
import service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;
    private final PlanRepository planRepository;

    // 1. Listar todos los planes disponibles
    @GetMapping("/planes")
    public ResponseEntity<List<Plan>> listarPlanes() {
        return ResponseEntity.ok(planRepository.findAll());
    }

    // 2. Crear nueva suscripción (Tu POST original)
    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> crearSuscripcion(@Valid @RequestBody SuscripcionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suscripcionService.crearSuscripcion(request));
    }

    // 3. Ver suscripción activa por miembro (Tu GET original)
    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<SuscripcionResponseDTO> obtenerSuscripcion(@PathVariable Long miembroId) {
        return ResponseEntity.ok(suscripcionService.obtenerSuscripcionPorMiembro(miembroId));
    }

    // 4. Cancelar suscripción (Eliminación lógica o física)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarSuscripcion(@PathVariable Long id) {
        suscripcionService.cancelarSuscripcion(id); // Asegúrate de implementar esto en tu Service
        return ResponseEntity.noContent().build();
    }

    // 5. Cambiar el estado de la suscripción (ej: de ACTIVA a CANCELADA)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        suscripcionService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok().build();
    }
}