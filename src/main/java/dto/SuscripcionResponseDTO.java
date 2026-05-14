package dto;

import java.time.LocalDate;

public record SuscripcionResponseDTO(
        Long id,
        Long miembroId,
        String nombrePlan,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String estado
) {}

