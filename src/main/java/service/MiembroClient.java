package service;

import config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Incluimos FeignConfig para que mande el JWT
@FeignClient(name = "ms-miembros", configuration = FeignConfig.class)
public interface MiembroClient {

    @GetMapping("/api/miembros/{id}/existe")
    boolean verificarMiembroExiste(@PathVariable("id") Long id);
}