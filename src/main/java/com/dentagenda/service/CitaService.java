package com.dentagenda.service;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.Cita;

public interface CitaService {
    Cita agendarCita(AgendarCitaDTO dto);
    Cita cancelarCita(Long id);
    Cita reprogramarCita(Long id, ReprogramarCitaDTO dto);
}