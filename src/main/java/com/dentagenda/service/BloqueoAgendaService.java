package com.dentagenda.service;

import java.time.LocalDate;
import java.util.List;

import com.dentagenda.dto.BloquearHorarioDTO;
import com.dentagenda.model.BloqueoAgenda;

public interface BloqueoAgendaService {
    BloqueoAgenda bloquearHorario(BloquearHorarioDTO dto);
    List<BloqueoAgenda> obtenerBloqueosPorFechaYRut(String rut, LocalDate fecha);
}