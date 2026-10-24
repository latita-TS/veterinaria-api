package com.veterinaria.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.veterinaria.api.entity.EstadoCita;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Datos que envía el cliente para crear (POST) o actualizar (PUT) una cita.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaRequest {

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 60, message = "El nombre de la mascota no puede superar 60 caracteres")
    private String nombreMascota;

    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 30, message = "La especie no puede superar 30 caracteres")
    private String especie;

    @NotBlank(message = "El nombre del dueño es obligatorio")
    @Size(max = 100, message = "El nombre del dueño no puede superar 100 caracteres")
    private String nombreDueno;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    private String telefonoDueno;

    @NotNull(message = "La fecha y hora de la cita es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 150, message = "El motivo no puede superar 150 caracteres")
    private String motivo;

    @Size(max = 100, message = "El nombre del veterinario no puede superar 100 caracteres")
    private String veterinario;

    /** Opcional. Si no se envía, la cita queda PENDIENTE. */
    private EstadoCita estado;
}
