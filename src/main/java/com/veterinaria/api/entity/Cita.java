package com.veterinaria.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tabla "citas": cada registro es una cita de una mascota en la clínica.
 */
@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_mascota", nullable = false, length = 60)
    private String nombreMascota;

    /** Perro, Gato, Conejo, etc. */
    @Column(nullable = false, length = 30)
    private String especie;

    @Column(name = "nombre_dueno", nullable = false, length = 100)
    private String nombreDueno;

    @Column(name = "telefono_dueno", nullable = false, length = 15)
    private String telefonoDueno;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** Vacunación, consulta general, baño, cirugía, etc. */
    @Column(nullable = false, length = 150)
    private String motivo;

    @Column(name = "veterinario", length = 100)
    private String veterinario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /** Se ejecuta automáticamente antes de insertar el registro. */
    @PrePersist
    public void antesDeGuardar() {
        this.fechaRegistro = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoCita.PENDIENTE;
        }
    }
}
