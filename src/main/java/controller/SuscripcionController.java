package com.gymapp.ms_suscripciones.controller;

import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> crearSuscripcion(@Valid @RequestBody SuscripcionRequestDTO request) {
        SuscripcionResponseDTO response = suscripcionService.crearSuscripcion(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/miembro/{miembroId}")
    public ResponseEntity<SuscripcionResponseDTO> obtenerSuscripcion(@PathVariable Long miembroId) {
        SuscripcionResponseDTO response = suscripcionService.obtenerSuscripcionPorMiembro(miembroId);
        return ResponseEntity.ok(response);
    }
}