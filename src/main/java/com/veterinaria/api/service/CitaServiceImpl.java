package com.veterinaria.api.service;

import com.veterinaria.api.dto.CitaRequest;
import com.veterinaria.api.dto.CitaResponse;
import com.veterinaria.api.entity.Cita;
import com.veterinaria.api.entity.EstadoCita;
import com.veterinaria.api.exception.RecursoNoEncontradoException;
import com.veterinaria.api.exception.ReglaNegocioException;
import com.veterinaria.api.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        return citaRepository.findAllByOrderByFechaHoraAsc()
                .stream()
                .map(CitaResponse::desdeEntidad)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse buscarPorId(Long id) {
        return CitaResponse.desdeEntidad(obtenerCita(id));
    }

    @Override
    @Transactional
    public CitaResponse registrar(CitaRequest request) {
        // Regla de negocio: no se puede agendar una cita en el pasado
        if (request.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha de la cita debe ser posterior a la fecha actual");
        }

        Cita cita = new Cita();
        copiarDatos(request, cita);
        cita.setEstado(request.getEstado() != null ? request.getEstado() : EstadoCita.PENDIENTE);

        return CitaResponse.desdeEntidad(citaRepository.save(cita));
    }

    @Override
    @Transactional
    public CitaResponse actualizar(Long id, CitaRequest request) {
        Cita cita = obtenerCita(id);

        // Regla de negocio: si la cita sigue pendiente, la nueva fecha no puede estar en el pasado
        EstadoCita nuevoEstado = request.getEstado() != null ? request.getEstado() : cita.getEstado();
        if (nuevoEstado == EstadoCita.PENDIENTE && request.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("Una cita pendiente no puede reprogramarse a una fecha pasada");
        }

        copiarDatos(request, cita);
        cita.setEstado(nuevoEstado);

        return CitaResponse.desdeEntidad(citaRepository.save(cita));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cita cita = obtenerCita(id);
        citaRepository.delete(cita);
    }

    private Cita obtenerCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una cita con id " + id));
    }

    private void copiarDatos(CitaRequest request, Cita cita) {
        cita.setNombreMascota(request.getNombreMascota().trim());
        cita.setEspecie(request.getEspecie().trim());
        cita.setNombreDueno(request.getNombreDueno().trim());
        cita.setTelefonoDueno(request.getTelefonoDueno().trim());
        cita.setFechaHora(request.getFechaHora());
        cita.setMotivo(request.getMotivo().trim());
        cita.setVeterinario(request.getVeterinario() != null ? request.getVeterinario().trim() : null);
    }
}
