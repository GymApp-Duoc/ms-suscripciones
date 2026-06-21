package service;

import com.gymapp.ms_suscripciones.assembler.SuscripcionAssembler;
import com.gymapp.ms_suscripciones.client.MiembroClient;
import com.gymapp.ms_suscripciones.client.NotificacionClient;
import com.gymapp.ms_suscripciones.dto.SuscripcionRequestDTO;
import com.gymapp.ms_suscripciones.dto.SuscripcionResponseDTO;
import com.gymapp.ms_suscripciones.exception.BusinessException;
import com.gymapp.ms_suscripciones.model.Suscripcion;
import com.gymapp.ms_suscripciones.repository.SuscripcionRepository;
import com.gymapp.ms_suscripciones.service.SuscripcionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionServiceImplTest {

    @Mock private SuscripcionRepository repository;
    @Mock private MiembroClient miembroClient;
    @Mock private NotificacionClient notificacionClient;
    @Mock private SuscripcionAssembler assembler;

    @InjectMocks private SuscripcionServiceImpl service;

    @Test
    void crear_PlanMensual_CalculaFechaCorrectamente() {
        SuscripcionRequestDTO request = new SuscripcionRequestDTO(1L, "MENSUAL", 29990.0);
        Suscripcion guardada = new Suscripcion(1L, 1L, "MENSUAL", LocalDate.now(), LocalDate.now().plusMonths(1), "ACTIVA", 29990.0);
        SuscripcionResponseDTO response = SuscripcionResponseDTO.builder().id(1L).estado("ACTIVA").build();

        when(miembroClient.obtenerPorId(1L)).thenReturn(new Object());
        when(repository.existsByMiembroIdAndEstado(1L, "ACTIVA")).thenReturn(false);
        when(repository.save(any(Suscripcion.class))).thenReturn(guardada);
        when(assembler.toResponseDTO(guardada)).thenReturn(response);

        SuscripcionResponseDTO resultado = service.crear(request);

        assertNotNull(resultado);
        assertEquals("ACTIVA", resultado.getEstado());
        verify(notificacionClient, times(1)).enviarNotificacion(any());
    }

    @Test
    void crear_SuscripcionActivaExistente_LanzaException() {
        SuscripcionRequestDTO request = new SuscripcionRequestDTO(1L, "ANUAL", 150000.0);

        when(miembroClient.obtenerPorId(1L)).thenReturn(new Object());
        when(repository.existsByMiembroIdAndEstado(1L, "ACTIVA")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.crear(request));
        assertEquals("El miembro ya posee una suscripción ACTIVA.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void cancelar_SuscripcionExistente_CambiaEstadoACancelada() {
        Suscripcion suscripcion = new Suscripcion(1L, 1L, "MENSUAL", LocalDate.now(), LocalDate.now(), "ACTIVA", 10.0);
        when(repository.findById(1L)).thenReturn(Optional.of(suscripcion));

        service.cancelar(1L);

        assertEquals("CANCELADA", suscripcion.getEstado());
        verify(repository, times(1)).save(suscripcion);
    }
}