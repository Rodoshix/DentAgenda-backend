package com.dentagenda.controller;

import com.dentagenda.dto.AgendarCitaDTO;
import com.dentagenda.dto.ReprogramarCitaDTO;
import com.dentagenda.model.Cita;
import com.dentagenda.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
}
