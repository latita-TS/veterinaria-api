package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Cita;
import com.veterinaria.api.entity.EstadoCita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de la CAPA DE ACCESO A DATOS (repositorio de citas).
 * Cubre las 4 operaciones pedidas en la rúbrica: insertar, actualizar, eliminar y listar.
 * Se ejecutan sobre una base de datos H2 en memoria, así no se tocan los datos reales.
 */
@DataJpaTest
class CitaRepositoryTest {

    @Autowired
    private CitaRepository citaRepository;

    private Cita citaBase;

    @BeforeEach
    void prepararDatos() {
        citaRepository.deleteAll();
        citaBase = Cita.builder()
                .nombreMascota("Firulais")
                .especie("Perro")
                .nombreDueno("Juan Pérez")
                .telefonoDueno("987654321")
                .fechaHora(LocalDateTime.now().plusDays(2).withSecond(0).withNano(0))
                .motivo("Vacuna antirrábica")
                .veterinario("Dra. Ana Torres")
                .build();
    }

    @Test
    @DisplayName("INSERTAR: guarda una cita y le asigna un id")
    void insertarCita() {
        Cita guardada = citaRepository.save(citaBase);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getEstado()).isEqualTo(EstadoCita.PENDIENTE);
        assertThat(guardada.getFechaRegistro()).isNotNull();
        assertThat(citaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("LISTAR: devuelve todas las citas ordenadas por fecha")
    void listarCitas() {
        citaRepository.save(citaBase);
        citaRepository.save(Cita.builder()
                .nombreMascota("Michi")
                .especie("Gato")
                .nombreDueno("María López")
                .telefonoDueno("912345678")
                .fechaHora(LocalDateTime.now().plusDays(1).withSecond(0).withNano(0))
                .motivo("Consulta general")
                .build());

        List<Cita> citas = citaRepository.findAllByOrderByFechaHoraAsc();

        assertThat(citas).hasSize(2);
        assertThat(citas.get(0).getNombreMascota()).isEqualTo("Michi");
        assertThat(citas.get(1).getNombreMascota()).isEqualTo("Firulais");
    }

    @Test
    @DisplayName("ACTUALIZAR: modifica el motivo y el estado de una cita")
    void actualizarCita() {
        Cita guardada = citaRepository.save(citaBase);

        guardada.setMotivo("Vacuna + desparasitación");
        guardada.setEstado(EstadoCita.ATENDIDA);
        citaRepository.save(guardada);

        Cita actualizada = citaRepository.findById(guardada.getId()).orElseThrow();
        assertThat(actualizada.getMotivo()).isEqualTo("Vacuna + desparasitación");
        assertThat(actualizada.getEstado()).isEqualTo(EstadoCita.ATENDIDA);
    }

    @Test
    @DisplayName("ELIMINAR: borra una cita y ya no se encuentra")
    void eliminarCita() {
        Cita guardada = citaRepository.save(citaBase);
        Long id = guardada.getId();

        citaRepository.deleteById(id);

        Optional<Cita> buscada = citaRepository.findById(id);
        assertThat(buscada).isEmpty();
        assertThat(citaRepository.count()).isZero();
    }

    @Test
    @DisplayName("BUSCAR POR ESTADO: filtra las citas canceladas")
    void buscarPorEstado() {
        citaRepository.save(citaBase);
        Cita cancelada = citaRepository.save(Cita.builder()
                .nombreMascota("Rocky")
                .especie("Perro")
                .nombreDueno("Luis Ramos")
                .telefonoDueno("955555555")
                .fechaHora(LocalDateTime.now().plusDays(3).withSecond(0).withNano(0))
                .motivo("Baño y corte")
                .estado(EstadoCita.CANCELADA)
                .build());

        List<Cita> canceladas = citaRepository.findByEstado(EstadoCita.CANCELADA);

        assertThat(canceladas).hasSize(1);
        assertThat(canceladas.get(0).getId()).isEqualTo(cancelada.getId());
    }
}
