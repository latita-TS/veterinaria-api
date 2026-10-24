package com.veterinaria.api.service;

import com.veterinaria.api.dto.CitaRequest;
import com.veterinaria.api.dto.CitaResponse;

import java.util.List;

/**
 * Capa de servicio: operaciones CRUD de citas.
 */
public interface CitaService {

    List<CitaResponse> listar();

    CitaResponse buscarPorId(Long id);

    CitaResponse registrar(CitaRequest request);

    CitaResponse actualizar(Long id, CitaRequest request);

    void eliminar(Long id);
}
