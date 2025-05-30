package com.dentagenda.service.impl;

import com.dentagenda.dto.PacienteCrearCuentaDTO;
import com.dentagenda.dto.RegistroPacienteDTO;
import com.dentagenda.model.Paciente;
import com.dentagenda.model.RolUsuario;
import com.dentagenda.model.Usuario;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.repository.UsuarioRepository;
import com.dentagenda.service.PacienteService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Paciente registrar(RegistroPacienteDTO dto) {
        if (pacienteRepository.findByRut(dto.getRut()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con este RUT.");
        }

        if (pacienteRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con este correo.");
        }

        Paciente paciente = new Paciente();
        paciente.setNombre(dto.getNombre());
        paciente.setRut(dto.getRut());
        paciente.setCorreo(dto.getCorreo());
        paciente.setTelefono(dto.getTelefono());

        // No se crea Usuario aún. Solo se guarda como ficha.
        return pacienteRepository.save(paciente);
    }
    
    @Override
    @Transactional
    public Paciente crearCuentaPaciente(PacienteCrearCuentaDTO dto) {
        Paciente paciente = pacienteRepository.findByRut(dto.getRut()).orElse(null);

        if (paciente != null) {
            // Ya fue registrado por recepcionista
            if (paciente.getUsuario() != null) {
                throw new RuntimeException("Este paciente ya tiene una cuenta creada.");
            }

            if (dto.getPassword() == null) {
                throw new RuntimeException("Debe ingresar una contraseña para crear su cuenta.");
            }

            Usuario usuario = Usuario.builder()
                    .rut(dto.getRut())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .rol(RolUsuario.PACIENTE)
                    .build();

            usuarioRepository.save(usuario);
            paciente.setUsuario(usuario);
            return pacienteRepository.save(paciente);
        }

        // Si el paciente no existe, validar que vengan todos los datos
        if (dto.getNombre() == null || dto.getCorreo() == null || dto.getTelefono() == null || dto.getPassword() == null) {
            throw new RuntimeException("Faltan datos obligatorios para registrar un nuevo paciente.");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .rut(dto.getRut())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(RolUsuario.PACIENTE)
                .build();

        usuarioRepository.save(nuevoUsuario);

        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setRut(dto.getRut());
        nuevoPaciente.setNombre(dto.getNombre());
        nuevoPaciente.setCorreo(dto.getCorreo());
        nuevoPaciente.setTelefono(dto.getTelefono());
        nuevoPaciente.setUsuario(nuevoUsuario);

        return pacienteRepository.save(nuevoPaciente);
    }

}