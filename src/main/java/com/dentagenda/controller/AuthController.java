package com.dentagenda.controller;

import com.dentagenda.dto.AuthRequest;
import com.dentagenda.dto.RefreshRequestDTO;
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

        String accessToken = jwtUtil.generateAccessToken(usuario.getRut(), usuario.getRol().name());
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getRut());

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);
        response.put("rut", usuario.getRut());
        response.put("rol", usuario.getRol().name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequestDTO request) {
        System.out.println("🔁 Intentando refrescar token: " + request.getRefreshToken());
        String refreshToken = request.getRefreshToken();

        try {
            // Validar token y extraer rut
            String rut = jwtUtil.extractUsername(refreshToken);

            // Validar existencia del usuario
            Usuario usuario = usuarioRepository.findByRut(rut)
                    .orElseThrow(() -> new RuntimeException("Usuario no existe"));

            // Verificar que el token esté vigente
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.status(403).body("Refresh token expirado o inválido");
            }

            // Generar nuevo access token
            String newAccessToken = jwtUtil.generateAccessToken(rut, usuario.getRol().name());

            Map<String, Object> response = new HashMap<>();
            response.put("access_token", newAccessToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(403).body("Token inválido");
        }
    }
}