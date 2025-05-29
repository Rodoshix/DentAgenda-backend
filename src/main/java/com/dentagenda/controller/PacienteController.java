package com.dentagenda.controller;

import com.dentagenda.dto.LoginPacienteDTO;
import com.dentagenda.dto.PacienteCrearCuentaDTO;
import com.dentagenda.dto.RegistroPacienteDTO;
import com.dentagenda.model.Paciente;
import com.dentagenda.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping("/registro")
    public ResponseEntity<Paciente> registrar(@Valid @RequestBody RegistroPacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.registrar(dto));
    }

    @PostMapping("/crear-cuenta")
    public ResponseEntity<Paciente> crearCuenta(@Valid @RequestBody PacienteCrearCuentaDTO dto) {
        return ResponseEntity.ok(pacienteService.crearCuentaPaciente(dto));
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginPacienteDTO dto) {
        pacienteService.autenticarPaciente(dto);
        return ResponseEntity.ok("Inicio de sesión exitoso");
    }
}