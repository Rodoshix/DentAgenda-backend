package com.dentagenda.service.impl;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.OdontologoDisponibilidadDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.BloqueoAgenda;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.model.Odontologo;
import com.dentagenda.model.Paciente;
import com.dentagenda.repository.BloqueoAgendaRepository;
import com.dentagenda.repository.CitaRepository;
import com.dentagenda.repository.OdontologoRepository;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private BloqueoAgendaRepository bloqueoAgendaRepository;

    @Override
    public Cita agendarCita(AgendarCitaDTO dto) {
        if (dto.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La fecha debe ser futura");
        }

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
        .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        Odontologo odontologo = odontologoRepository.findById(dto.getOdontologoId())
        .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        if (citaRepository.existsByFechaHoraAndOdontologo(dto.getFechaHora(), odontologo)) {
            throw new RuntimeException("El odontólogo ya tiene una cita en ese horario");
        }

        List<BloqueoAgenda> bloqueos = bloqueoAgendaRepository
            .findByOdontologoRutAndFecha(odontologo.getRut(), dto.getFechaHora().toLocalDate());

        boolean estaBloqueado = bloqueos.stream().anyMatch(bloqueo ->
            !dto.getFechaHora().toLocalTime().isBefore(bloqueo.getHoraInicio()) &&
            !dto.getFechaHora().toLocalTime().isAfter(bloqueo.getHoraFin())
        );

        if (estaBloqueado) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El odontólogo tiene su agenda bloqueada en ese horario."
            );
        }

        Cita cita = new Cita();
        cita.setFechaHora(dto.getFechaHora());
        cita.setOdontologo(odontologo);
        cita.setPaciente(paciente);
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setMotivo(dto.getMotivo());

        return citaRepository.save(cita);
    }
    @Override
    public Cita cancelarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya está cancelada");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        return citaRepository.save(cita);
    }

    @Override
    public Cita reprogramarCita(Long id, ReprogramarCitaDTO dto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new RuntimeException("No se puede reprogramar una cita cancelada");
        }

        if (dto.getNuevaFechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La nueva fecha debe ser futura");
        }

        boolean yaExiste = citaRepository.existsByFechaHoraAndOdontologo(dto.getNuevaFechaHora(), cita.getOdontologo());
        if (yaExiste) {
            throw new RuntimeException("El odontólogo ya tiene una cita en esa fecha/hora");
        }

        cita.setFechaHora(dto.getNuevaFechaHora());
        return citaRepository.save(cita);
    }

    @Override
    public List<Cita> obtenerCitasPorPaciente(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        return citaRepository.findByPaciente(paciente);
    }

    @Override
    public List<Cita> buscarCitasPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return citaRepository.findByFechaHoraBetween(desde, hasta);
    }

    @Override
    public List<Cita> buscarCitasPorEstado(EstadoCita estado) {
        return citaRepository.findByEstado(estado);
    }

    @Override
    public Page<Cita> buscarCitasPorOdontologo(String nombreOdontologo, Pageable pageable) {
        Odontologo odontologo = odontologoRepository.findByNombreIgnoreCase(nombreOdontologo)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
        return citaRepository.findByOdontologo(odontologo, pageable);
    }

    @Override
    public List<Cita> obtenerCitasFuturasPorOdontologo(String nombreOdontologo) {
        Odontologo odontologo = odontologoRepository.findByNombreIgnoreCase(nombreOdontologo)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        return citaRepository.findByFechaHoraAfterAndOdontologo(LocalDateTime.now(), odontologo);
    }

    @Override
    public List<Cita> obtenerHistorialPorOdontologo(Long odontologoId) {
        return citaRepository.findByOdontologo_Id(odontologoId);
    }

    @Override
    public List<OdontologoDisponibilidadDTO> consultarDisponibilidadPorFecha(LocalDate fecha) {
        List<Odontologo> odontologos = odontologoRepository.findAll();
        List<OdontologoDisponibilidadDTO> respuesta = new ArrayList<>();

        for (Odontologo od : odontologos) {
            LocalDateTime desde = fecha.atTime(0, 0);
            LocalDateTime hasta = fecha.atTime(23, 59);
            List<Cita> citas = citaRepository.findByOdontologoAndFechaHoraBetween(od, desde, hasta);
            List<BloqueoAgenda> bloqueos = bloqueoAgendaRepository.findByOdontologoRutAndFecha(od.getRut(), fecha);

            List<String> horas = generarHorasDelDia();  // Ej: 09:00 a 18:00 cada 30 min

            // Filtrar citas agendadas
            Set<String> horasOcupadas = citas.stream()
                .map(c -> c.getFechaHora().toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toSet());

            // Filtrar horas bloqueadas
            for (BloqueoAgenda b : bloqueos) {
                horas.removeIf(h -> {
                    LocalTime t = LocalTime.parse(h);
                    return !t.isBefore(b.getHoraInicio()) && !t.isAfter(b.getHoraFin());
                });
            }

            // Remover horas con citas
            horas.removeIf(horasOcupadas::contains);

            OdontologoDisponibilidadDTO dto = new OdontologoDisponibilidadDTO();
            dto.setOdontologoId(od.getId());
            dto.setNombre(od.getNombre());
            dto.setHorasDisponibles(horas);

            respuesta.add(dto);
        }

        return respuesta;
    }

    private List<String> generarHorasDelDia() {
        List<String> horas = new ArrayList<>();
        LocalTime inicio = LocalTime.of(9, 0);
        LocalTime fin = LocalTime.of(18, 0);

        while (!inicio.isAfter(fin)) {
            horas.add(inicio.toString().substring(0, 5));
            inicio = inicio.plusHours(1);;
        }

        return horas;
    }
}