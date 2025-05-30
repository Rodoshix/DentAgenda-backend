package com.dentagenda.controller;

import com.dentagenda.dto.RegistrarTratamientoDTO;
import com.dentagenda.model.Tratamiento;
import com.dentagenda.service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @PostMapping("/registrar")
    public Tratamiento registrar(@RequestBody RegistrarTratamientoDTO dto) {
        return tratamientoService.registrarTratamiento(dto);
    }

    @GetMapping("/paciente/{rut}")
    public List<Tratamiento> listarPorPaciente(@PathVariable String rut) {
        return tratamientoService.obtenerTratamientosPorRutPaciente(rut);
    }
}