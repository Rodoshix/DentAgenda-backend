package com.dentagenda.service.impl;

import com.dentagenda.dto.RegistrarTratamientoDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.Tratamiento;
import com.dentagenda.repository.CitaRepository;
import com.dentagenda.repository.TratamientoRepository;
import com.dentagenda.service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;

@Service
public class TratamientoServiceImpl implements TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Override
    public Tratamiento registrarTratamiento(RegistrarTratamientoDTO dto) {
        Cita cita = citaRepository.findById(dto.getIdCita())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        Tratamiento t = new Tratamiento();
        t.setDiagnostico(dto.getDiagnostico());
        t.setProcedimiento(dto.getProcedimiento());
        t.setFecha(LocalDate.now());
        t.setCita(cita);
        t.setPaciente(cita.getPaciente());
        t.setOdontologo(cita.getOdontologo());

        return tratamientoRepository.save(t);
    }

    @Override
    public List<Tratamiento> obtenerTratamientosPorRutPaciente(String rut) {
    return tratamientoRepository.findByPacienteRut(rut);
}
}