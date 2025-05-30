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

public interface CitaService {
    Cita agendarCita(AgendarCitaDTO dto);
    Cita cancelarCita(Long id);
    Cita reprogramarCita(Long id, ReprogramarCitaDTO dto);
    List<Cita> obtenerCitasPorPaciente(Long pacienteId);
    List<Cita> buscarCitasPorFecha(LocalDateTime desde, LocalDateTime hasta);
    List<Cita> buscarCitasPorEstado(EstadoCita estado);
    Page<Cita> buscarCitasPorOdontologo(String odontologo, Pageable pageable);
    List<Cita> obtenerCitasFuturasPorOdontologo(String odontologo);
    List<Cita> obtenerHistorialPorOdontologo(Long odontologoId);
    List<OdontologoDisponibilidadDTO> consultarDisponibilidadPorFecha(LocalDate fecha);
    Cita confirmarAsistencia(Long id);
}