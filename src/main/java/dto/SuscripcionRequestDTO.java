package dto;

import jakarta.validation.constraints.NotNull;

public record SuscripcionRequestDTO(
        @NotNull(message = "El ID del miembro es obligatorio")
        Long miembroId,

        @NotNull(message = "El ID del plan es obligatorio")
        Long planId
) {}