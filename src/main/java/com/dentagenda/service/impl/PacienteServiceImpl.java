package com.dentagenda.service.impl;

import com.dentagenda.dto.RegistroPacienteDTO;
import com.dentagenda.model.Paciente;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public Paciente registrar(RegistroPacienteDTO dto) {
        if (pacienteRepository.findByRut(dto.getRut()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con este RUT.");
        }

        if (pacienteRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con este correo.");
        }

        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setRut(dto.getRut());
        paciente.setCorreo(dto.getCorreo());
        paciente.setTelefono(dto.getTelefono());

        return pacienteRepository.save(paciente);
    }
}