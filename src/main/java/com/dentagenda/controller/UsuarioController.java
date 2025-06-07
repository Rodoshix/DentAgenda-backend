package com.dentagenda.controller;

import com.dentagenda.dto.CambiarPasswordDTO;
import com.dentagenda.dto.EditarPerfilDTO;
import com.dentagenda.dto.PerfilPacienteDTO;
import com.dentagenda.dto.RecuperarPasswordDTO;
import com.dentagenda.dto.RestablecerPasswordDTO;
import com.dentagenda.model.Paciente;
import com.dentagenda.model.Odontologo;
import com.dentagenda.model.Recepcionista;
import com.dentagenda.model.Usuario;
import com.dentagenda.model.RolUsuario;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.repository.OdontologoRepository;
import com.dentagenda.repository.RecepcionistaRepository;
import com.dentagenda.service.UsuarioCuentaService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioCuentaService cuentaService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private RecepcionistaRepository recepcionistaRepository;

    public UsuarioController(UsuarioCuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    // 1. Obtener perfil de usuario autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = cuentaService.obtenerPerfil(userDetails);

        String rut = usuario.getRut();
        RolUsuario rol = usuario.getRol();

        if (rol == RolUsuario.PACIENTE) {
            Paciente paciente = pacienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("No se encontró el paciente asociado"));
            PerfilPacienteDTO dto = new PerfilPacienteDTO(
                rut,
                paciente.getNombre(),
                paciente.getCorreo(),
                paciente.getTelefono(),
                rol.name()
            );
            return ResponseEntity.ok(dto);
        }

        if (rol == RolUsuario.ODONTOLOGO) {
            Odontologo odontologo = odontologoRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("No se encontró el odontólogo asociado"));
            PerfilPacienteDTO dto = new PerfilPacienteDTO(
                rut,
                odontologo.getNombre(),
                odontologo.getCorreo(),
                odontologo.getTelefono(),
                rol.name()
            );
            return ResponseEntity.ok(dto);
        }

        if (rol == RolUsuario.RECEPCIONISTA) {
            Recepcionista recepcionista = recepcionistaRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("No se encontró el recepcionista asociado"));
            PerfilPacienteDTO dto = new PerfilPacienteDTO(
                rut,
                recepcionista.getNombre(),
                recepcionista.getCorreo(),
                recepcionista.getTelefono(),
                rol.name()
            );
            return ResponseEntity.ok(dto);
        }

        log.warn("Usuario con rol desconocido accedió al perfil: {}", usuario.getRut());
        
        PerfilPacienteDTO dto = new PerfilPacienteDTO(
            rut,
            usuario.getNombre(),
            null,
            null,
            rol.name()
        );
        return ResponseEntity.ok(dto);
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
