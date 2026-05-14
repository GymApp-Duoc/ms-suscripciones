package com.gymapp.ms_suscripciones.repository;

import com.gymapp.ms_suscripciones.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}

