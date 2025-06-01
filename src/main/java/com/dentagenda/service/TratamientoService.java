package com.dentagenda.service;

import com.dentagenda.dto.RegistrarTratamientoDTO;
import com.dentagenda.model.Tratamiento;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public interface TratamientoService {
    Tratamiento registrarTratamiento(RegistrarTratamientoDTO dto);
    List<Tratamiento> obtenerTratamientosPorRutPaciente(String rut, UserDetails userDetails);
    Tratamiento obtenerPorCita(Long citaId, UserDetails userDetails);
    Tratamiento editarTratamiento(Long id, RegistrarTratamientoDTO dto, UserDetails userDetails);
}