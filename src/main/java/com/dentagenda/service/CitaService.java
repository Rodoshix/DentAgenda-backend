package com.dentagenda.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.OdontologoDisponibilidadDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface CitaService {
    Cita agendarCita(AgendarCitaDTO dto);
    Cita cancelarCita(Long id);
    Cita reprogramarCita(Long id, ReprogramarCitaDTO dto);
    List<OdontologoDisponibilidadDTO> consultarDisponibilidadPorFecha(LocalDate fecha);
    Cita confirmarAsistencia(Long id);

    List<Cita> obtenerCitasPorPaciente(Long pacienteId, UserDetails userDetails);
    List<Cita> obtenerHistorialPorOdontologo(Long odontologoId, UserDetails userDetails);
    List<Cita> obtenerCitasFuturasPorOdontologo(UserDetails userDetails);
    Page<Cita> buscarCitasPorOdontologo(String nombre, Pageable pageable, UserDetails userDetails);
    List<Cita> buscarCitasPorFecha(LocalDateTime desde, LocalDateTime hasta, UserDetails userDetails);
    List<Cita> buscarCitasPorEstado(EstadoCita estado, Long odontologoId, UserDetails userDetails);
    List<Cita> obtenerCitasPorFechaYOdontologo(LocalDate fecha, Long odontologoId);
}
