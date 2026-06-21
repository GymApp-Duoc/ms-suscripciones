package com.gymapp.ms_suscripciones.repository;

import com.gymapp.ms_suscripciones.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    List<Suscripcion> findByMiembroId(Long miembroId);
    boolean existsByMiembroIdAndEstado(Long miembroId, String estado);



    // REPORTE 1: Contar suscripciones por estado
    long countByEstado(String estado);

    // REPORTE 2: Filtrar por tipo de plan y estado
    List<Suscripcion> findByTipoPlanIgnoreCaseAndEstado(String tipoPlan, String estado);

    // REPORTE 3: Suscripciones próximas a vencer
    List<Suscripcion> findByEstadoAndFechaFinBetween(String estado, LocalDate fechaInicio, LocalDate fechaFin);

    // REPORTE 4: Cálculo de ingresos totales de suscripciones activas
    @Query("SELECT COALESCE(SUM(s.precio), 0.0) FROM Suscripcion s WHERE s.estado = 'ACTIVA'")
    Double calcularIngresosTotalesActivos();

    // REPORTE 5: Nuevas suscripciones desde una fecha de corte
    List<Suscripcion> findByFechaInicioAfterAndEstado(LocalDate fecha, String estado);
}