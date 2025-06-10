package com.dentagenda.service.impl;

import com.dentagenda.dto.RegistrarTratamientoDTO;
import com.dentagenda.dto.TratamientoDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.Tratamiento;
import com.dentagenda.repository.CitaRepository;
import com.dentagenda.repository.TratamientoRepository;
import com.dentagenda.service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

        // Validar que la cita no tenga tratamiento
        if (tratamientoRepository.existsByCita(cita)) {
            throw new RuntimeException("Ya existe un tratamiento registrado para esta cita.");
        }

        // Validar estado de la cita
        switch (cita.getEstado()) {
            case CANCELADA -> throw new RuntimeException("No se puede registrar un tratamiento para una cita cancelada.");
            case PENDIENTE -> throw new RuntimeException("La cita aún no ha sido confirmada.");
            default -> {} // CONFIRMADA o ATENDIDA
        }

        // Validar odontólogo autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String rutAuth = auth.getName(); // El rut viene del token
        String rutOdontologo = cita.getOdontologo().getRut();

        if (!rutAuth.equals(rutOdontologo)) {
            throw new RuntimeException("Solo el odontólogo que atendió la cita puede registrar el tratamiento.");
        }

        // Asignar observación
        cita.setObservacion(dto.getObservacion());
        citaRepository.save(cita); // ✅ Solo una vez

        // Crear tratamiento
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
    public List<TratamientoDTO> obtenerTratamientosPorRutPaciente(String rut, UserDetails userDetails) {
        String rutAuth = userDetails.getUsername();
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_PACIENTE".equals(rol) && !rut.equals(rutAuth)) {
            throw new RuntimeException("No puedes consultar el historial de otro paciente.");
        }

        return tratamientoRepository.findByPacienteRut(rut).stream()
            .map(t -> new TratamientoDTO(
                t.getId(),
                t.getFecha().toString(),
                t.getDiagnostico(),
                t.getProcedimiento(),
                t.getCita().getObservacion(),
                t.getPaciente().getNombre()
            ))
            .toList();
    }

    @Override
    public Tratamiento obtenerPorCita(Long citaId, UserDetails userDetails) {
        Tratamiento tratamiento = tratamientoRepository.findByCita_Id(citaId)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado para esta cita."));

        String rutAuth = userDetails.getUsername();
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_PACIENTE".equals(rol)) {
            if (!tratamiento.getPaciente().getRut().equals(rutAuth)) {
                throw new RuntimeException("No puedes ver tratamientos de otros pacientes.");
            }
        }

        if ("ROLE_ODONTOLOGO".equals(rol)) {
            if (!tratamiento.getOdontologo().getRut().equals(rutAuth)) {
                throw new RuntimeException("No puedes ver tratamientos registrados por otros odontólogos.");
            }
        }

        return tratamiento;
    }

    @Override
    public Tratamiento editarTratamiento(Long id, RegistrarTratamientoDTO dto, UserDetails userDetails) {
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));

        String rutAuth = userDetails.getUsername();
        String rutOdontologo = tratamiento.getOdontologo().getRut();

        if (!rutAuth.equals(rutOdontologo)) {
            throw new RuntimeException("No tienes permiso para editar este tratamiento.");
        }

        tratamiento.setDiagnostico(dto.getDiagnostico());
        tratamiento.setProcedimiento(dto.getProcedimiento());

        return tratamientoRepository.save(tratamiento);
    }
}