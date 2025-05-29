package com.dentagenda.service.impl;

import com.dentagenda.dto.PacienteCrearCuentaDTO;
import com.dentagenda.dto.RegistroPacienteDTO;
import com.dentagenda.dto.LoginPacienteDTO;
import com.dentagenda.model.Paciente;
import com.dentagenda.repository.PacienteRepository;
import com.dentagenda.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
        private PasswordEncoder passwordEncoder;

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

        return pacienteRepository.save(paciente);
    }

    @Override
    public Paciente crearCuentaPaciente(PacienteCrearCuentaDTO dto) {
        Paciente paciente = pacienteRepository.findByRut(dto.getRut()).orElse(null);

        if (paciente != null) {
            if (paciente.getContrasena() != null) {
                throw new RuntimeException("Este paciente ya tiene una cuenta creada.");
            }
            // Si fue registrado por la recepcionista pero no tiene cuenta aún
            paciente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
            return pacienteRepository.save(paciente);
        }

        // Si no existe, verificar que vengan todos los datos
        if (dto.getNombre() == null || dto.getCorreo() == null || dto.getTelefono() == null) {
            throw new RuntimeException("Faltan datos obligatorios para registrar un nuevo paciente.");
        }

        Paciente nuevo = new Paciente();
        nuevo.setRut(dto.getRut());
        nuevo.setNombre(dto.getNombre());
        nuevo.setCorreo(dto.getCorreo());
        nuevo.setTelefono(dto.getTelefono());
        nuevo.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        return pacienteRepository.save(nuevo);
    }

    @Override
    public boolean autenticarPaciente(LoginPacienteDTO dto) {
        Paciente paciente = pacienteRepository.findByRut(dto.getRut())
                .orElseThrow(() -> new RuntimeException("RUT no registrado"));

        if (paciente.getContrasena() == null) {
            throw new RuntimeException("Este paciente no tiene cuenta web");
        }

        boolean contrasenaCorrecta = passwordEncoder.matches(dto.getContrasena(), paciente.getContrasena());

        if (!contrasenaCorrecta) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return true;
    }
}