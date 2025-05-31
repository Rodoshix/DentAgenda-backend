package com.dentagenda.service;

import com.dentagenda.dto.RegistroPacienteDTO;
import com.dentagenda.model.Paciente;
import com.dentagenda.dto.PacienteCrearCuentaDTO;

import java.util.List;

public interface PacienteService {
    Paciente registrar(RegistroPacienteDTO dto);
    Paciente crearCuentaPaciente(PacienteCrearCuentaDTO dto);
    List<Paciente> listarPacientes();
    Paciente obtenerPorRut(String rut);
}
