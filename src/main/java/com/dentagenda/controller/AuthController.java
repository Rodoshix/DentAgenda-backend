package com.dentagenda.controller;

import com.dentagenda.dto.AuthRequest;
import com.dentagenda.model.Paciente;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest dto) {
        Paciente paciente = pacienteRepository.findByRut(dto.getRut())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (paciente.getPassword() == null || !passwordEncoder.matches(dto.getPassword(), paciente.getPassword())) {
            return ResponseEntity.status(403).body("Credenciales inválidas");
        }

        // Aquí puedes establecer el rol como "PACIENTE", o usar un campo real si lo tuvieras
        String token = jwtUtil.generateToken(paciente.getRut(), "PACIENTE");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("nombre", paciente.getNombre());
        response.put("rut", paciente.getRut());

        return ResponseEntity.ok(response);
    }
}