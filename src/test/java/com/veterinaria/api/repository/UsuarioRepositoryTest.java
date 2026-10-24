package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba que la contraseña se guarda CIFRADA en la base de datos.
 */
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("La contraseña se almacena como hash BCrypt y no en texto plano")
    void passwordSeGuardaCifrada() {
        usuarioRepository.save(Usuario.builder()
                .username("recepcion1")
                .password(encoder.encode("clave123"))
                .nombreCompleto("Rosa Díaz")
                .rol("RECEPCION")
                .build());

        Usuario guardado = usuarioRepository.findByUsername("recepcion1").orElseThrow();

        assertThat(guardado.getPassword()).isNotEqualTo("clave123");
        assertThat(guardado.getPassword()).startsWith("$2a$");
        assertThat(encoder.matches("clave123", guardado.getPassword())).isTrue();
        assertThat(usuarioRepository.existsByUsername("recepcion1")).isTrue();
    }
}
