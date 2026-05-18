package com.gymapp.ms_suscripciones.client;

import com.gymapp.ms_suscripciones.dto.NotificacionRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "ms-notificaciones", url = "${ms.notificaciones.url:http://localhost:8091}")
public interface NotificacionClient {

    @PostMapping("/api/notificaciones")
    void enviarNotificacion(@RequestBody NotificacionRequestDTO request);
}