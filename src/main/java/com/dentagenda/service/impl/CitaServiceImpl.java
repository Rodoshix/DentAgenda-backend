package com.dentagenda.service.impl;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.CitaAgendaDTO;
import com.dentagenda.dto.CitaCalendarioDTO;
import com.dentagenda.dto.CitaDTO;
import com.dentagenda.dto.CitaHistorialDTO;
import com.dentagenda.dto.HoraDisponibilidadDTO;
import com.dentagenda.dto.OdontologoDisponibilidadDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.BloqueoAgenda;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.model.Odontologo;
import com.dentagenda.model.Paciente;
import com.dentagenda.model.Tratamiento;
import com.dentagenda.repository.BloqueoAgendaRepository;
import com.dentagenda.repository.CitaRepository;
import com.dentagenda.repository.OdontologoRepository;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.repository.TratamientoRepository;
import com.dentagenda.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Override
    public Cita agendarCita(AgendarCitaDTO dto, String rutPaciente) {
        if (dto.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La fecha debe ser futura");
        }

        LocalDateTime fechaHoraRedondeada = dto.getFechaHora()
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        Paciente paciente = pacienteRepository.findByUsuarioRut(rutPaciente)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Odontologo odontologo = odontologoRepository.findById(dto.getOdontologoId())
            .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        Cita cita = new Cita();
        cita.setFechaHora(fechaHoraRedondeada);
        cita.setPaciente(paciente);
        cita.setOdontologo(odontologo);
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setMotivo(dto.getMotivo());

        boolean yaExiste = citaRepository.existsByFechaHoraAndOdontologoAndEstadoNot(
            fechaHoraRedondeada, odontologo, EstadoCita.CANCELADA
        );
        if (yaExiste) {
            throw new RuntimeException("El odontólogo ya tiene una cita en esa fecha/hora");
        }

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

    public List<CitaCalendarioDTO> obtenerCitasParaCalendarioPorRut(String rut) {
        Paciente paciente = pacienteRepository.findByUsuarioRut(rut)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        List<Cita> citas = citaRepository.findByPaciente(paciente);

        return citas.stream().map(cita -> {
            CitaCalendarioDTO dto = new CitaCalendarioDTO();
            dto.setFechaHoraInicio(cita.getFechaHora().toString());
            dto.setFechaHoraFin(cita.getFechaHora().plusMinutes(30).toString());
            dto.setOdontologoNombre(cita.getOdontologo().getNombre());
            return dto;
        }).toList();
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

    @Override
    public List<String> obtenerHorasDisponiblesPorOdontologo(Long odontologoId, LocalDate fecha) {
        Odontologo od = odontologoRepository.findById(odontologoId)
            .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
    
        LocalDateTime desde = fecha.atTime(0, 0);
        LocalDateTime hasta = fecha.atTime(23, 59);
    
        List<Cita> citas = citaRepository.findByOdontologoAndFechaHoraBetweenAndEstadoNot(
            od, desde, hasta, EstadoCita.CANCELADA
        );
    
        List<BloqueoAgenda> bloqueos = bloqueoAgendaRepository.findByOdontologoRutAndFecha(od.getRut(), fecha);
        List<String> horas = generarHorasDelDia(); // usa tu método actual
    
        // Citas ocupadas
        Set<String> horasOcupadas = citas.stream()
            .map(c -> c.getFechaHora().toLocalTime().toString().substring(0, 5))
            .collect(Collectors.toSet());
    
        // Bloqueos
        for (BloqueoAgenda b : bloqueos) {
            horas.removeIf(h -> {
                LocalTime t = LocalTime.parse(h);
                return !t.isBefore(b.getHoraInicio()) && !t.isAfter(b.getHoraFin());
            });
        }
    
        // Elimina horas ocupadas por citas
        horas.removeIf(horasOcupadas::contains);
    
        return horas;
    }

    @Override
    public List<CitaHistorialDTO> obtenerHistorialCitasPaciente(String rutPaciente) {
        Paciente paciente = pacienteRepository.findByUsuarioRut(rutPaciente)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        List<Cita> citas = citaRepository.findByPaciente(paciente);

            return citas.stream().map(cita -> {
        CitaHistorialDTO dto = new CitaHistorialDTO();
        dto.setId(cita.getId());
        dto.setFechaHora(cita.getFechaHora());
        dto.setEstado(cita.getEstado().name());
        dto.setObservacion(cita.getObservacion());
        dto.setOdontologoNombre(
        cita.getOdontologo() != null ? cita.getOdontologo().getNombre() : "N/A"
        );

        // AQUÍ
        Tratamiento tratamiento = tratamientoRepository.findByCita(cita).orElse(null);
        dto.setTratamiento(tratamiento != null
            ? tratamiento.getProcedimiento() + " - " + tratamiento.getDiagnostico()
            : null);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void agendarCitaNoUsuario(CitaDTO dto) {
        // Validar campos
        if (dto.getPacienteId() == null || dto.getOdontologoId() == null || dto.getFechaHora() == null || dto.getMotivo() == null) {
            throw new IllegalArgumentException("Faltan datos para registrar la cita.");
        }

        // Convertir fechaHora a LocalDateTime
        LocalDateTime fechaHora = LocalDateTime.parse(dto.getFechaHora());

        // Validar disponibilidad (si ya existe una cita a esa hora)
        boolean ocupada = citaRepository.existsByOdontologo_IdAndFechaHora(dto.getOdontologoId(), fechaHora);
        if (ocupada) {
            throw new IllegalArgumentException("El horario ya está ocupado.");
        }

        // Crear y guardar la cita
        Cita cita = new Cita();
        cita.setPaciente(pacienteRepository.findById(dto.getPacienteId())
            .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado")));
        cita.setOdontologo(odontologoRepository.findById(dto.getOdontologoId())
            .orElseThrow(() -> new IllegalArgumentException("Odontólogo no encontrado")));
        cita.setFechaHora(fechaHora);
        cita.setMotivo(dto.getMotivo());
        cita.setEstado(EstadoCita.PENDIENTE);

        citaRepository.save(cita);
    }

    @Override
    public List<HoraDisponibilidadDTO> consultarDisponibilidadPorOdontologoYFecha(Long odontologoId, LocalDate fecha) {
        Odontologo odontologo = odontologoRepository.findById(odontologoId)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        LocalDateTime inicio = fecha.atTime(9, 0);
        LocalDateTime fin = fecha.atTime(18, 0);

        List<Cita> citas = citaRepository.findByOdontologoAndFechaHoraBetween(odontologo, inicio, fin);

        List<String> todasLasHoras = generarHorasPosibles(); // método auxiliar
        List<HoraDisponibilidadDTO> disponibilidad = new ArrayList<>();

        for (String horaStr : todasLasHoras) {
            LocalTime hora = LocalTime.parse(horaStr);
            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

            Optional<Cita> cita = citas.stream()
                    .filter(c -> c.getFechaHora().equals(fechaHora))
                    .findFirst();

            HoraDisponibilidadDTO dto = new HoraDisponibilidadDTO();
            dto.setHora(horaStr);
            dto.setPaciente(cita.map(c -> c.getPaciente().getNombre()).orElse(null)); // si hay cita, poner nombre
            disponibilidad.add(dto);
        }

        return disponibilidad;
    }

    private List<String> generarHorasPosibles() {
        List<String> horas = new ArrayList<>();
        LocalTime inicio = LocalTime.of(9, 0);
        LocalTime fin = LocalTime.of(18, 0);
    
        while (inicio.isBefore(fin)) {
            horas.add(inicio.toString().substring(0, 5)); // HH:mm
            inicio = inicio.plusHours(1);
        }
        return horas;
    }

    @Override
    public List<CitaAgendaDTO> obtenerCitasDelOdontologoPorFecha(UserDetails userDetails, LocalDate fecha) {
        String rutToken = userDetails.getUsername().replace("-", "").toLowerCase(); // RUT del token sin guion

        // Buscar al odontólogo por RUT, sin importar guion o mayúsculas
        Odontologo odontologo = odontologoRepository.findAll().stream()
            .filter(o -> o.getRut().replace("-", "").equalsIgnoreCase(rutToken))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado (por RUT: " + rutToken + ")"));

        LocalDateTime inicio = fecha.atTime(0, 0);
        LocalDateTime fin = fecha.atTime(23, 59);

        List<Cita> citas = citaRepository.findByOdontologoAndFechaHoraBetween(odontologo, inicio, fin);

        return citas.stream()
            .map(c -> new CitaAgendaDTO(
                c.getId(),
                c.getFechaHora().toLocalTime().toString(),
                c.getPaciente().getNombre(),
                c.getMotivo(),
                c.getEstado().name()
            ))
            .toList();
    }
}