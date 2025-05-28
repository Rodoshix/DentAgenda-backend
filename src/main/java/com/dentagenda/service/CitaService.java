package com.dentagenda.service;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.model.Cita;

public interface CitaService {
    Cita agendarCita(AgendarCitaDTO dto);
}