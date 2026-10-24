package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Cita;
import com.veterinaria.api.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Capa de acceso a datos para citas.
 * Hereda de JpaRepository los métodos save, findAll, findById, deleteById, etc.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByOrderByFechaHoraAsc();

    List<Cita> findByEstado(EstadoCita estado);
}
