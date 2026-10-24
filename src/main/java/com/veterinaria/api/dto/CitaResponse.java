package com.veterinaria.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.veterinaria.api.entity.Cita;
import com.veterinaria.api.entity.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Datos de una cita que la API devuelve al cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaResponse {

    private Long id;
    private String nombreMascota;
    private String especie;
    private String nombreDueno;
    private String telefonoDueno;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHora;

    private String motivo;
    private String veterinario;
    private EstadoCita estado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaRegistro;

    /** Convierte una entidad Cita en su respuesta (DTO). */
    public static CitaResponse desdeEntidad(Cita cita) {
        return CitaResponse.builder()
                .id(cita.getId())
                .nombreMascota(cita.getNombreMascota())
                .especie(cita.getEspecie())
                .nombreDueno(cita.getNombreDueno())
                .telefonoDueno(cita.getTelefonoDueno())
                .fechaHora(cita.getFechaHora())
                .motivo(cita.getMotivo())
                .veterinario(cita.getVeterinario())
                .estado(cita.getEstado())
                .fechaRegistro(cita.getFechaRegistro())
                .build();
    }
}
