package com.dentagenda.repository;

import com.dentagenda.model.Paciente;
import com.dentagenda.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByRut(String rut);

    Optional<Paciente> findByCorreo(String correo);

    Optional<Paciente> findByUsuario(Usuario usuario);
}