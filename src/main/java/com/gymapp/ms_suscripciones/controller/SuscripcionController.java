package com.gymapp.ms_suscripciones.controller;

import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.service.SuscripcionServiceInt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/suscripciones")
@RequiredArgsConstructor
@Tag(name = "Suscripciones y Facturación", description = "Operaciones de pago, renovación y estado de acceso de miembros")
public class SuscripcionController {

    private final SuscripcionServiceInt service;

    @Operation(summary = "Obtener historial completo de suscripciones")
    @GetMapping
    public ResponseEntity<List<SuscripcionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @Operation(summary = "Obtener detalle de una suscripción por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todas las suscripciones (activas/canceladas) de un miembro")
    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarPorMiembro(@PathVariable Long miembroId) {
        return ResponseEntity.ok(service.buscarPorMiembro(miembroId));
    }

    @Operation(summary = "Endpoint interno: Verifica si el miembro tiene acceso al gimnasio (Retorna 200 OK o 404 Not Found)")
    @GetMapping("/miembro/{miembroId}/estado")
    public ResponseEntity<Void> verificarEstado(@PathVariable Long miembroId) {
        List<SuscripcionResponseDTO> suscripciones = service.buscarPorMiembro(miembroId);
        boolean tieneSuscripcionActiva = suscripciones.stream().anyMatch(s -> "ACTIVA".equalsIgnoreCase(s.getEstado()));

        if (tieneSuscripcionActiva) {
            return ResponseEntity.ok().build(); // Autorizado
        }
        return ResponseEntity.notFound().build(); // Bloqueado
    }

    @Operation(summary = "Registrar un nuevo pago y activar suscripción")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Suscripción activada con éxito"),
            @ApiResponse(responseCode = "400", description = "Miembro ya tiene suscripción activa o datos inválidos")
    })
    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> crear(@Valid @RequestBody SuscripcionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @Operation(summary = "Cancelar una suscripción (Baja lógica)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(summary = "Reporte 1: Total de miembros con suscripción activa")
    @GetMapping("/reportes/activas/total")
    public ResponseEntity<Long> contarActivas() {
        return ResponseEntity.ok(service.contarSuscripcionesActivas());
    }

    @Operation(summary = "Reporte 2: Ingresos totales generados por suscripciones activas actuales")
    @GetMapping("/reportes/ingresos")
    public ResponseEntity<Double> calcularIngresos() {
        return ResponseEntity.ok(service.calcularIngresosTotales());
    }

    @Operation(summary = "Reporte 3: Desglose de suscripciones por plan (MENSUAL, TRIMESTRAL, ANUAL)")
    @GetMapping("/reportes/plan/{tipoPlan}")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarPorPlan(@PathVariable String tipoPlan) {
        return ResponseEntity.ok(service.listarPorTipoPlan(tipoPlan));
    }

    @Operation(summary = "Reporte 4: Alerta de retención - Suscripciones que expiran en los próximos X días")
    @GetMapping("/reportes/vencimientos")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarProximosVencimientos(@RequestParam(defaultValue = "7") int dias) {
        return ResponseEntity.ok(service.listarProximasAVencer(dias));
    }

    @Operation(summary = "Reporte 5: Suscripciones nuevas contratadas en los últimos X días")
    @GetMapping("/reportes/recientes")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarNuevasSuscripciones(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(service.listarSuscripcionesRecientes(dias));
    }
}