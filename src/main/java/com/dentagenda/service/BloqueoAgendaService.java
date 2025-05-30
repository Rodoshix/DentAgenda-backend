package com.dentagenda.service;

import com.dentagenda.dto.BloquearHorarioDTO;
import com.dentagenda.model.BloqueoAgenda;

public interface BloqueoAgendaService {
    BloqueoAgenda bloquearHorario(BloquearHorarioDTO dto);
}