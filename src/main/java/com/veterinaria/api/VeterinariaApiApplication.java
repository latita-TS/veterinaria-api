package com.veterinaria.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST "VetCitas".
 * Al ejecutar esta clase, Spring Boot levanta el servidor en el puerto 8080.
 */
@SpringBootApplication
public class VeterinariaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VeterinariaApiApplication.class, args);
    }
}
