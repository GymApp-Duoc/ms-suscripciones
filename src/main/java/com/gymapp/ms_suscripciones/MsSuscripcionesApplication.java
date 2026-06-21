package com.gymapp.ms_suscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MsSuscripcionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSuscripcionesApplication.class, args);
	}
}

