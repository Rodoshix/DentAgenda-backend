package com.dentagenda.service.impl;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.model.Paciente;
import com.dentagenda.repository.CitaRepository;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public Cita agendarCita(AgendarCitaDTO dto) {
        if (dto.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La fecha debe ser futura");
        }

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (citaRepository.existsByFechaHoraAndOdontologo(dto.getFechaHora(), dto.getOdontologo())) {
            throw new RuntimeException("El odontólogo ya tiene una cita en ese horario");
        }

        Cita cita = new Cita();
        cita.setFechaHora(dto.getFechaHora());
        cita.setOdontologo(dto.getOdontologo());
        cita.setPaciente(paciente);
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setMotivo(dto.getMotivo());

        return citaRepository.save(cita);
    }
}