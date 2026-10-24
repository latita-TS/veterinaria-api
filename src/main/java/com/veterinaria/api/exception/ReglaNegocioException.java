package com.veterinaria.api.exception;

/**
 * Se lanza cuando los datos no cumplen una regla del negocio (responde 400).
 * Ejemplo: registrar una cita en una fecha que ya pasó.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
