package com.veterinaria.api.service;

import com.veterinaria.api.dto.LoginRequest;
import com.veterinaria.api.dto.LoginResponse;
import com.veterinaria.api.dto.MensajeResponse;
import com.veterinaria.api.dto.RegistroRequest;

/**
 * Capa de servicio: lógica de autenticación.
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);

    MensajeResponse registrar(RegistroRequest request);
}
