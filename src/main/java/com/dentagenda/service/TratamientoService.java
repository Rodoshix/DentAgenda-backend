package com.dentagenda.service;

import com.dentagenda.dto.RegistrarTratamientoDTO;
import com.dentagenda.model.Tratamiento;

import java.util.List;

public interface TratamientoService {
    Tratamiento registrarTratamiento(RegistrarTratamientoDTO dto);
    List<Tratamiento> obtenerTratamientosPorRutPaciente(String rut);
}