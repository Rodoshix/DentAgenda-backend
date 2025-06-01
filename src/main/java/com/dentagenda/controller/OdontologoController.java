package com.dentagenda.controller;

import com.dentagenda.dto.RegistroOdontologoDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.model.Odontologo;
import com.dentagenda.service.CitaService;
import com.dentagenda.service.OdontologoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/odontologos")
public class OdontologoController {

    @Autowired
    private OdontologoService odontologoService;

    @Autowired
    private CitaService citaService;

    @PostMapping("/registro")
    public ResponseEntity<Odontologo> registrar(@Valid @RequestBody RegistroOdontologoDTO dto) {
        return ResponseEntity.ok(odontologoService.registrarOdontologo(dto));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable Long id) {
        odontologoService.eliminarOdontologo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Odontologo>> listarTodos() {
        return ResponseEntity.ok(odontologoService.listarTodos());
    }
    
    @GetMapping("/agenda/fecha")
    public ResponseEntity<List<Cita>> agendaPorFechaYOdontologo(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("odontologoId") Long odontologoId) {
        return ResponseEntity.ok(citaService.obtenerCitasPorFechaYOdontologo(fecha, odontologoId));
    }
}