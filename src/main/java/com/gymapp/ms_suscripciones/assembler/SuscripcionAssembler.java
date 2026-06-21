package com.gymapp.ms_suscripciones.assembler;

import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.model.Suscripcion;
import org.springframework.stereotype.Component;

@Component
public class SuscripcionAssembler {

    public SuscripcionResponseDTO toResponseDTO(Suscripcion suscripcion) {
        if (suscripcion == null) {
            return null;
        }

        return SuscripcionResponseDTO.builder()
                .id(suscripcion.getId())
                .miembroId(suscripcion.getMiembroId())
                .tipoPlan(suscripcion.getTipoPlan())
                .fechaInicio(suscripcion.getFechaInicio())
                .fechaFin(suscripcion.getFechaFin())
                .estado(suscripcion.getEstado())
                .precio(suscripcion.getPrecio())
                .build();
    }
}