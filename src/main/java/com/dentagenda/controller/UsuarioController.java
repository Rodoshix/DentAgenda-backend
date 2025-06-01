package com.dentagenda.controller;

import com.dentagenda.dto.CambiarPasswordDTO;
import com.dentagenda.dto.EditarPerfilDTO;
import com.dentagenda.dto.RecuperarPasswordDTO;
import com.dentagenda.dto.RestablecerPasswordDTO;
import com.dentagenda.model.Usuario;
import com.dentagenda.service.UsuarioCuentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioCuentaService cuentaService;

    public UsuarioController(UsuarioCuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    // 1. Obtener perfil de usuario autenticado
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario perfil = cuentaService.obtenerPerfil(userDetails);
        return ResponseEntity.ok(perfil);
    }

    // 2. Cambiar contraseña desde el perfil
    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestBody @Valid CambiarPasswordDTO dto) {
        cuentaService.cambiarPassword(userDetails, dto);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // 3. Solicitar recuperación de contraseña
    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@RequestBody @Valid RecuperarPasswordDTO dto) {
        cuentaService.enviarRecuperacion(dto);
        return ResponseEntity.ok("Correo de recuperación enviado");
    }

    // 4. Restablecer contraseña con token
    @PutMapping("/restablecer-password")
    public ResponseEntity<?> restablecerPassword(@RequestBody @Valid RestablecerPasswordDTO dto) {
        cuentaService.restablecerPassword(dto);
        return ResponseEntity.ok("Contraseña restablecida correctamente");
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> editarPerfil(
            @Valid @RequestBody EditarPerfilDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        cuentaService.editarPerfil(userDetails.getUsername(), dto);
        return ResponseEntity.ok("Perfil actualizado exitosamente.");
    }
} 
