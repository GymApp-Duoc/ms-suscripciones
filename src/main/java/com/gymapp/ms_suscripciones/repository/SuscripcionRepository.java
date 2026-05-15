package com.gymapp.ms_suscripciones.repository;

import com.gymapp.ms_suscripciones.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    List<Suscripcion> findByMiembroId(Long miembroId);
    boolean existsByMiembroIdAndEstado(Long miembroId, String estado);
}

