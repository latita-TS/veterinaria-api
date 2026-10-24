package com.veterinaria.api.controller;

import com.veterinaria.api.dto.CitaRequest;
import com.veterinaria.api.dto.CitaResponse;
import com.veterinaria.api.dto.MensajeResponse;
import com.veterinaria.api.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Servicios web REST del CRUD de citas (requieren token JWT).
 */
@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    /** GET /api/citas -> lista todas las citas. */
    @GetMapping
    public ResponseEntity<List<CitaResponse>> listar() {
        return ResponseEntity.ok(citaService.listar());
    }

    /** GET /api/citas/{id} -> obtiene una cita por su id. */
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.buscarPorId(id));
    }

    /** POST /api/citas -> registra una nueva cita. */
    @PostMapping
    public ResponseEntity<CitaResponse> registrar(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.registrar(request));
    }

    /** PUT /api/citas/{id} -> actualiza una cita existente. */
    @PutMapping("/{id}")
    public ResponseEntity<CitaResponse> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.actualizar(id, request));
    }

    /** DELETE /api/citas/{id} -> elimina una cita. */
    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Cita con id " + id + " eliminada correctamente"));
    }
}
