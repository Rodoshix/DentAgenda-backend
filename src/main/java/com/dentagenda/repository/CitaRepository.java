package com.dentagenda.repository;

import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.model.Odontologo;
import com.dentagenda.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPaciente(Paciente paciente);

    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    boolean existsByFechaHoraAndOdontologo(LocalDateTime fechaHora, Odontologo odontologo);

    List<Cita> findByEstado(EstadoCita estado);

    Page<Cita> findByOdontologoContainingIgnoreCase(String odontologo, Pageable pageable);

    List<Cita> findByFechaHoraAfterAndOdontologo(LocalDateTime fechaHora, Odontologo odontologo);

    Page<Cita> findByOdontologo(Odontologo odontologo, Pageable pageable);
    
    List<Cita> findByOdontologo_Id(Long odontologoId);

    List<Cita> findByOdontologoAndFechaHoraBetweenAndEstadoNot(
    Odontologo odontologo,LocalDateTime inicio,LocalDateTime fin,EstadoCita estado
    );
}