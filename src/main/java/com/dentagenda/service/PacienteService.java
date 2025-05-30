package com.dentagenda.service;

import com.dentagenda.dto.RegistroPacienteDTO;
import com.dentagenda.model.Paciente;
import com.dentagenda.dto.PacienteCrearCuentaDTO;

public interface PacienteService {
    Paciente registrar(RegistroPacienteDTO dto);
    Paciente crearCuentaPaciente(PacienteCrearCuentaDTO dto);
}
