package com.gymapp.ms_suscripciones.controller;

import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.service.SuscripcionServiceInt;
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
public class SuscripcionController {

    private final SuscripcionServiceInt service;

    @GetMapping
    public ResponseEntity<List<SuscripcionResponseDTO>> obtenerTodas() {
        log.info("Petición REST recibida: Listar todas las suscripciones");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición REST recibida: Obtener suscripción con ID {}", id);
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarPorMiembro(@PathVariable Long miembroId) {
        log.info("Petición REST recibida: Listar suscripciones para el miembro ID {}", miembroId);
        return ResponseEntity.ok(service.buscarPorMiembro(miembroId));
    }

    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> crear(@Valid @RequestBody SuscripcionRequestDTO dto) {
        log.info("Petición REST recibida: Crear nueva suscripción para miembro ID {}", dto.getMiembroId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición REST recibida: Cancelar suscripción con ID {}", id);
        if (service.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
