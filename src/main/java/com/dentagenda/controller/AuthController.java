package com.dentagenda.controller;

import com.dentagenda.dto.AuthRequest;
import com.dentagenda.model.Usuario;
import com.dentagenda.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest dto) {
        Usuario usuario = usuarioRepository.findByRut(dto.getRut())
                .orElseThrow(() -> new RuntimeException("RUT no registrado"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(403).body("Contraseña inválida");
        }

        String token = jwtUtil.generateToken(usuario.getRut(), usuario.getRol().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("rut", usuario.getRut());
        response.put("rol", usuario.getRol().name());

        return ResponseEntity.ok(response);
    }
}