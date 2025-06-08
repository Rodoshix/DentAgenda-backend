package com.dentagenda.controller;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.CitaPacienteDTO;
import com.dentagenda.dto.OdontologoDisponibilidadDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.model.Paciente;
import com.dentagenda.model.RolUsuario;
import com.dentagenda.model.Usuario;
import com.dentagenda.repository.CitaRepository;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.service.CitaService;
import com.dentagenda.service.UsuarioCuentaService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioCuentaService usuarioCuentaService;
    
    @PostMapping("/agendar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<Cita> agendarCita(
        @Valid @RequestBody AgendarCitaDTO dto,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String rut = userDetails.getUsername();
        return ResponseEntity.ok(citaService.agendarCita(dto, rut));
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Cita> cancelarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    @PutMapping("/citas/{id}/reprogramar")
    public ResponseEntity<?> reprogramarCita(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        String nuevaFechaHoraStr = body.get("nuevaFechaHora");

        if (nuevaFechaHoraStr == null || nuevaFechaHoraStr.isBlank()) {
            return ResponseEntity.badRequest().body("Debe proporcionar una nueva fecha y hora");
        }

        try {
            LocalDateTime nuevaFechaHora = LocalDateTime.parse(nuevaFechaHoraStr);

            Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

            if (!cita.getEstado().equals(EstadoCita.PENDIENTE)) {
                return ResponseEntity.badRequest().body("Solo se pueden reprogramar citas pendientes");
            }

            // Impedir reprogramar el mismo día
            LocalDate fechaActual = LocalDate.now();
            LocalDate fechaCita = nuevaFechaHora.toLocalDate();

            if (fechaCita.isEqual(fechaActual)) {
                return ResponseEntity.badRequest().body("No se puede reprogramar una cita el mismo día");
            }

            if (citaRepository.existsByOdontologoIdAndFechaHoraAndIdNot(
                cita.getOdontologo().getId(), nuevaFechaHora, cita.getId())) {
                return ResponseEntity.badRequest().body("El odontólogo ya tiene una cita agendada en esa fecha y hora");
            }

            cita.setFechaHora(nuevaFechaHora);
            citaRepository.save(cita);

            return ResponseEntity.ok("Cita reprogramada correctamente");

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Se espera: YYYY-MM-DDTHH:mm:00");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al reprogramar la cita");
        }
    }

    @GetMapping("/mis-citas")
    public ResponseEntity<?> obtenerCitasDelPaciente(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioCuentaService.obtenerPerfil(userDetails);

        if (!usuario.getRol().equals(RolUsuario.PACIENTE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso no autorizado");
        }

        Optional<Paciente> pacienteOpt = pacienteRepository.findByUsuario(usuario);

        if (pacienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado");
        }

        Paciente paciente = pacienteOpt.get();

        List<Cita> citas = citaRepository.findByPaciente(paciente);

        List<CitaPacienteDTO> citasDTO = citas.stream().map(c -> {
            CitaPacienteDTO dto = new CitaPacienteDTO();
            dto.setId(c.getId());
            dto.setFechaHora(c.getFechaHora());
            dto.setEstado(c.getEstado().toString());

            if (c.getOdontologo() != null) {
                dto.setOdontologoId(c.getOdontologo().getId());
                dto.setOdontologoNombre(c.getOdontologo().getNombre());
            }

            dto.setObservacion(c.getObservacion());

            return dto;
        }).toList();

        return ResponseEntity.ok(citasDTO);
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<Cita>> obtenerCitasPorFecha(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @AuthenticationPrincipal UserDetails userDetails) {
    
        return ResponseEntity.ok(citaService.buscarCitasPorFecha(desde, hasta, userDetails));
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Cita>> obtenerCitasPorEstado(
            @RequestParam("estado") EstadoCita estado,
            @RequestParam(value = "odontologoId", required = false) Long odontologoId,
            @AuthenticationPrincipal UserDetails userDetails) {
    
        return ResponseEntity.ok(citaService.buscarCitasPorEstado(estado, odontologoId, userDetails));
    }
    
    @GetMapping("/buscar-por-odontologo")
    public ResponseEntity<Page<Cita>> obtenerCitasPorOdontologo(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(citaService.buscarCitasPorOdontologo(nombre, pageable, userDetails));
    }

    @GetMapping("/futuras/odontologo")
    public ResponseEntity<List<Cita>> obtenerCitasFuturasPorOdontologo(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(citaService.obtenerCitasFuturasPorOdontologo(userDetails));
    }

    @GetMapping("/odontologo/{id}")
    public ResponseEntity<List<Cita>> obtenerHistorialOdontologo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(citaService.obtenerHistorialPorOdontologo(id, userDetails));
    }

    @GetMapping("/disponibilidad")
    public List<OdontologoDisponibilidadDTO> disponibilidad(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return citaService.consultarDisponibilidadPorFecha(fecha);
    }

    @PutMapping("/confirmar-asistencia/{id}")
    public ResponseEntity<Cita> confirmarAsistencia(@PathVariable Long id) {
        Cita cita = citaService.confirmarAsistencia(id);
        return ResponseEntity.ok(cita);
    }
}
