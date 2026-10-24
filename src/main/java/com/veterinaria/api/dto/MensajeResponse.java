package com.veterinaria.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta simple con un mensaje (por ejemplo, al eliminar una cita).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {

    private String mensaje;
}
