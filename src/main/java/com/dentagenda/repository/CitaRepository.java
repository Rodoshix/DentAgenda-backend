package com.dentagenda.repository;

import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPaciente(Paciente paciente);

    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    boolean existsByFechaHoraAndOdontologo(LocalDateTime fechaHora, String odontologo);

    List<Cita> findByEstado(EstadoCita estado);
}