package com.dentagenda.service.impl;

import com.dentagenda.dto.CambiarPasswordDTO;
import com.dentagenda.dto.EditarPerfilDTO;
import com.dentagenda.dto.RecuperarPasswordDTO;
import com.dentagenda.dto.RestablecerPasswordDTO;
import com.dentagenda.model.Usuario;
import com.dentagenda.repository.OdontologoRepository;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.repository.RecepcionistaRepository;
import com.dentagenda.repository.UsuarioRepository;
import com.dentagenda.service.UsuarioCuentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioCuentaServiceImpl implements UsuarioCuentaService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private RecepcionistaRepository recepcionistaRepository;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioCuentaServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario obtenerPerfil(UserDetails userDetails) {
        String rut = userDetails.getUsername();
        return usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public void cambiarPassword(UserDetails userDetails, CambiarPasswordDTO dto) {
        Usuario usuario = usuarioRepository.findByRut(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));
        usuarioRepository.save(usuario);
    }

    @Override
    public void enviarRecuperacion(RecuperarPasswordDTO dto) {
        // Aquí deberías generar un token, guardarlo y enviar un correo. Por ahora se deja como estructura vacía.
        Optional<Usuario> usuarioOpt = usuarioRepository.findByRut(dto.getRut());
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("No se encontró un usuario con ese RUT");
        }

        // TODO: Implementar lógica real de recuperación (email, token, etc.)
        System.out.println("Se simula envío de correo con token de recuperación a " + dto.getRut());
    }

    @Override
    public void restablecerPassword(RestablecerPasswordDTO dto) {
        // Aquí deberías validar el token. Por ahora se deja como ejemplo simple.
        Optional<Usuario> usuarioOpt = usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getRut().equals("12345678-9")) // Ejemplo simulado
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Token inválido o expirado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));
        usuarioRepository.save(usuario);
    }
    
    @Override
    public void editarPerfil(String rut, EditarPerfilDTO dto) {
        pacienteRepository.findByRut(rut).ifPresent(paciente -> {
            paciente.setNombre(dto.getNombre());
            paciente.setCorreo(dto.getCorreo());
            paciente.setTelefono(dto.getTelefono());
            pacienteRepository.save(paciente);
        });

        odontologoRepository.findByRut(rut).ifPresent(od -> {
            od.setNombre(dto.getNombre());
            od.setCorreo(dto.getCorreo());
            od.setTelefono(dto.getTelefono());
            odontologoRepository.save(od);
        });

        recepcionistaRepository.findByRut(rut).ifPresent(recep -> {
            recep.setNombre(dto.getNombre());
            recep.setCorreo(dto.getCorreo());
            recep.setTelefono(dto.getTelefono());
            recepcionistaRepository.save(recep);
        });
    }
}