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
import org.springframework.security.core.userdetails.UserDetails;

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

        // Redondear la fecha/hora a la hora exacta
        LocalDateTime fechaHoraRedondeada = dto.getFechaHora()
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
        .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        Odontologo odontologo = odontologoRepository.findById(dto.getOdontologoId())
        .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // Validar si ya hay una cita no cancelada en esa hora exacta
            if (citaRepository.existsByFechaHoraAndOdontologoAndEstadoNot(
                fechaHoraRedondeada, odontologo, EstadoCita.CANCELADA)) {
            throw new RuntimeException("El odontólogo ya tiene una cita en ese horario");
        }

        List<BloqueoAgenda> bloqueos = bloqueoAgendaRepository
            .findByOdontologoRutAndFecha(odontologo.getRut(), dto.getFechaHora().toLocalDate());

        boolean estaBloqueado = bloqueos.stream().anyMatch(bloqueo ->
            !fechaHoraRedondeada.toLocalTime().isBefore(bloqueo.getHoraInicio()) &&
            !fechaHoraRedondeada.toLocalTime().isAfter(bloqueo.getHoraFin())
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

        // Redondear la nueva fecha/hora a la hora exacta
        LocalDateTime fechaHoraRedondeada = dto.getNuevaFechaHora()
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        if (fechaHoraRedondeada.isBefore(LocalDateTime.now())) {
                throw new RuntimeException("La nueva fecha debe ser futura");
        }

        // Validar si hay una cita no cancelada en esa hora exacta (excepto esta misma cita)
        boolean yaExiste = citaRepository.existsByFechaHoraAndOdontologoAndEstadoNot(
            fechaHoraRedondeada, cita.getOdontologo(), EstadoCita.CANCELADA);

        if (yaExiste) {
            throw new RuntimeException("El odontólogo ya tiene una cita en esa fecha/hora");
        }

        cita.setFechaHora(fechaHoraRedondeada);
        return citaRepository.save(cita);
    }

    @Override
    public List<Cita> obtenerCitasPorPaciente(Long pacienteId, UserDetails userDetails) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        String rutUsuario = userDetails.getUsername(); // El RUT del usuario autenticado
        String rol = userDetails.getAuthorities().iterator().next().getAuthority(); // Ej: ROLE_PACIENTE

        // Si es paciente: solo puede ver su propio historial
        if ("ROLE_PACIENTE".equals(rol)) {
            if (!paciente.getRut().equals(rutUsuario)) {
                throw new RuntimeException("No tienes permiso para ver citas de otro paciente.");
            }
        }

        // Si es recepcionista: acceso total
        // Si el día de mañana hay más roles, puedes manejar lógica extra aquí

        return citaRepository.findByPaciente(paciente);
    }

    @Override
    public List<Cita> buscarCitasPorFecha(LocalDateTime desde, LocalDateTime hasta, UserDetails userDetails) {
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();
    
        if ("ROLE_RECEPCIONISTA".equals(rol)) {
            return citaRepository.findByFechaHoraBetween(desde, hasta);
        }
    
        if ("ROLE_ODONTOLOGO".equals(rol)) {
            String rut = userDetails.getUsername();
            Odontologo odontologo = odontologoRepository.findByRut(rut)
                    .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
            return citaRepository.findByFechaHoraBetweenAndOdontologo(desde, hasta, odontologo);
        }
    
        throw new RuntimeException("No tienes permiso para ver citas por fecha.");
    }

    @Override
    public List<Cita> buscarCitasPorEstado(EstadoCita estado, Long odontologoId, UserDetails userDetails) {
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();
    
        if ("ROLE_RECEPCIONISTA".equals(rol)) {
            if (odontologoId != null) {
                Odontologo od = odontologoRepository.findById(odontologoId)
                        .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
                return citaRepository.findByEstadoAndOdontologo(estado, od);
            } else {
                return citaRepository.findByEstado(estado);
            }
        }
    
        if ("ROLE_ODONTOLOGO".equals(rol)) {
            String rut = userDetails.getUsername();
            Odontologo od = odontologoRepository.findByRut(rut)
                    .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
            return citaRepository.findByEstadoAndOdontologo(estado, od);
        }
    
        throw new RuntimeException("No tienes permiso para esta acción.");
    }

    @Override
    public Page<Cita> buscarCitasPorOdontologo(String nombre, Pageable pageable, UserDetails userDetails) {
        String rutUsuario = userDetails.getUsername();
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();
    
        if ("ROLE_ODONTOLOGO".equals(rol)) {
            Odontologo odontologo = odontologoRepository.findByRut(rutUsuario)
                    .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
            return citaRepository.findByOdontologo(odontologo, pageable);
        }
    
        if ("ROLE_RECEPCIONISTA".equals(rol)) {
            if (nombre == null || nombre.isBlank()) {
                throw new RuntimeException("Debe proporcionar el nombre del odontólogo.");
            }
            Odontologo odontologo = odontologoRepository.findByNombreIgnoreCase(nombre)
                    .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
            return citaRepository.findByOdontologo(odontologo, pageable);
        }
    
        throw new RuntimeException("No tienes permiso para realizar esta acción.");
    }

    @Override
    public List<Cita> obtenerCitasFuturasPorOdontologo(UserDetails userDetails) {
        String rut = userDetails.getUsername();
        Odontologo odontologo = odontologoRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        return citaRepository.findByFechaHoraAfterAndOdontologo(LocalDateTime.now(), odontologo);
    }

    @Override
    public List<Cita> obtenerHistorialPorOdontologo(Long odontologoId, UserDetails userDetails) {
        Odontologo odontologo = odontologoRepository.findById(odontologoId)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        String rutUsuario = userDetails.getUsername(); // RUT del usuario autenticado
        String rol = userDetails.getAuthorities().iterator().next().getAuthority(); // Ej: ROLE_ODONTOLOGO

        if ("ROLE_ODONTOLOGO".equals(rol)) {
            if (!odontologo.getRut().equals(rutUsuario)) {
                throw new RuntimeException("No tienes permiso para ver el historial de otro odontólogo.");
            }
        }

        return citaRepository.findByOdontologo_Id(odontologoId);
    }

    @Override
    public List<OdontologoDisponibilidadDTO> consultarDisponibilidadPorFecha(LocalDate fecha) {
        List<Odontologo> odontologos = odontologoRepository.findAll();
        List<OdontologoDisponibilidadDTO> respuesta = new ArrayList<>();

        for (Odontologo od : odontologos) {
            LocalDateTime desde = fecha.atTime(0, 0);
            LocalDateTime hasta = fecha.atTime(23, 59);
            List<Cita> citas = citaRepository.findByOdontologoAndFechaHoraBetweenAndEstadoNot(
                od, desde, hasta, EstadoCita.CANCELADA
            );
            List<BloqueoAgenda> bloqueos = bloqueoAgendaRepository.findByOdontologoRutAndFecha(od.getRut(), fecha);

            List<String> horas = generarHorasDelDia();

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

    @Override
    public Cita confirmarAsistencia(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new RuntimeException("Solo se puede confirmar una cita pendiente.");
        }

        cita.setEstado(EstadoCita.CONFIRMADA);
        return citaRepository.save(cita);
    }

    @Override
    public List<Cita> obtenerCitasPorFechaYOdontologo(LocalDate fecha, Long odontologoId) {
        Odontologo odontologo = odontologoRepository.findById(odontologoId)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        LocalDateTime desde = fecha.atTime(0, 0);
        LocalDateTime hasta = fecha.atTime(23, 59);

        return citaRepository.findByOdontologoAndFechaHoraBetween(odontologo, desde, hasta);
    }
}