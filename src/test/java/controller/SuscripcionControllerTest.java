package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymapp.ms_suscripciones.controller.SuscripcionController;
import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.service.SuscripcionServiceInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionControllerTest {

    private MockMvc mockMvc;
    @Mock private SuscripcionServiceInt service;
    @InjectMocks private SuscripcionController controller;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void crear_PeticionValida_Retorna201() throws Exception {

        SuscripcionRequestDTO request = new SuscripcionRequestDTO(1L, "MENSUAL", 30000.0);
        SuscripcionResponseDTO response = SuscripcionResponseDTO.builder().id(2L).estado("ACTIVA").build();

        when(service.crear(any(SuscripcionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/suscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    void crear_PrecioNegativo_Retorna400BadRequest() throws Exception {

        SuscripcionRequestDTO request = new SuscripcionRequestDTO(1L, "ANUAL", -500.0);


        mockMvc.perform(post("/api/suscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verificarEstado_MiembroConAcceso_Retorna200() throws Exception {

        SuscripcionResponseDTO subActiva = SuscripcionResponseDTO.builder().estado("ACTIVA").build();
        when(service.buscarPorMiembro(1L)).thenReturn(List.of(subActiva));


        mockMvc.perform(get("/api/suscripciones/miembro/1/estado"))
                .andExpect(status().isOk());
    }

    @Test
    void verificarEstado_MiembroBloqueado_Retorna404() throws Exception {

        SuscripcionResponseDTO subCancelada = SuscripcionResponseDTO.builder().estado("CANCELADA").build();
        when(service.buscarPorMiembro(1L)).thenReturn(List.of(subCancelada));


        mockMvc.perform(get("/api/suscripciones/miembro/1/estado"))
                .andExpect(status().isNotFound());
    }
}