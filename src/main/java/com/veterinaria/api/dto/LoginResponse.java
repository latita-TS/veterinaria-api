package com.veterinaria.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta del login: incluye el token JWT que se usará en las demás peticiones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String mensaje;
    private String token;
    private String tipo;
    private long expiraEnMs;
    private String username;
    private String nombreCompleto;
    private String rol;
}
