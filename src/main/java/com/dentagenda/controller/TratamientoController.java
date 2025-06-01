package com.dentagenda.controller;

import com.dentagenda.dto.RegistrarTratamientoDTO;
import com.dentagenda.model.Tratamiento;
import com.dentagenda.service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @PostMapping("/registrar")
    public Tratamiento registrar(@RequestBody RegistrarTratamientoDTO dto) {
        return tratamientoService.registrarTratamiento(dto);
    }

    @GetMapping("/paciente/{rut}")
    public List<Tratamiento> listarPorPaciente(
            @PathVariable String rut,
            @AuthenticationPrincipal UserDetails userDetails) {
        return tratamientoService.obtenerTratamientosPorRutPaciente(rut, userDetails);
    }

    @GetMapping("/cita/{id}")
    public ResponseEntity<Tratamiento> obtenerPorCita(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tratamientoService.obtenerPorCita(id, userDetails));
    }

    @PutMapping("/{id}/editar")
    public ResponseEntity<Tratamiento> editarTratamiento(
            @PathVariable Long id,
            @RequestBody RegistrarTratamientoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tratamientoService.editarTratamiento(id, dto, userDetails));
    }
}