package com.dentagenda.service;

import com.dentagenda.dto.RegistroPacienteDTO;
import com.dentagenda.model.Paciente;

public interface PacienteService {
    Paciente registrar(RegistroPacienteDTO dto);
}
