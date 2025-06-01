package com.dentagenda.controller;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.OdontologoDisponibilidadDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.service.CitaService;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/agendar")
    public ResponseEntity<Cita> agendarCita(@Valid @RequestBody AgendarCitaDTO dto) {
        return ResponseEntity.ok(citaService.agendarCita(dto));
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Cita> cancelarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<Cita> reprogramarCita(@PathVariable Long id, @Valid @RequestBody ReprogramarCitaDTO dto) {
        return ResponseEntity.ok(citaService.reprogramarCita(id, dto));
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<Cita>> obtenerHistorialPaciente(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(id, userDetails));
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
