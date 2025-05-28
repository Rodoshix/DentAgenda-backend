package com.dentagenda.controller;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.service.CitaService;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Cita>> obtenerCitasPorPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(id));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<Cita>> obtenerCitasPorFecha(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(citaService.buscarCitasPorFecha(desde, hasta));
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Cita>> obtenerCitasPorEstado(@RequestParam("estado") EstadoCita estado) {
        return ResponseEntity.ok(citaService.buscarCitasPorEstado(estado));
    }
}
