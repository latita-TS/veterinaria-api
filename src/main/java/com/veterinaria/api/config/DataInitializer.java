package com.veterinaria.api.config;

import com.veterinaria.api.entity.Usuario;
import com.veterinaria.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Al iniciar la aplicación crea el usuario administrador si todavía no existe.
 * La contraseña se guarda cifrada con BCrypt.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByUsername(adminUsername)) {
            usuarioRepository.save(Usuario.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .nombreCompleto("Administrador VetCitas")
                    .rol("ADMIN")
                    .build());
            log.info("Usuario administrador '{}' creado con contraseña cifrada (BCrypt)", adminUsername);
        }
    }
}
