package com.gymapp.ms_suscripciones.repository;

import com.gymapp.ms_suscripciones.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    List<Suscripcion> findByMiembroId(Long miembroId);
    Optional<Suscripcion> findByMiembroIdAndEstado(Long miembroId, String estado);
}

