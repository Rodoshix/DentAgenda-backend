package com.dentagenda.service;

import java.time.LocalDateTime;
import java.util.List;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;

public interface CitaService {
    Cita agendarCita(AgendarCitaDTO dto);
    Cita cancelarCita(Long id);
    Cita reprogramarCita(Long id, ReprogramarCitaDTO dto);
    List<Cita> obtenerCitasPorPaciente(Long pacienteId);
    List<Cita> buscarCitasPorFecha(LocalDateTime desde, LocalDateTime hasta);
    List<Cita> buscarCitasPorEstado(EstadoCita estado);
}