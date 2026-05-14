package com.gymapp.ms_suscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsSuscripcionesApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsSuscripcionesApplication.class, args);
	}
}