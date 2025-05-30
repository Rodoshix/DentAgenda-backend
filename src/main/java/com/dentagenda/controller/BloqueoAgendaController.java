package com.dentagenda.controller;

import com.dentagenda.dto.BloquearHorarioDTO;
import com.dentagenda.model.BloqueoAgenda;
import com.dentagenda.service.BloqueoAgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bloqueos")
public class BloqueoAgendaController {

    @Autowired
    private BloqueoAgendaService bloqueoAgendaService;

    @PostMapping("/registrar")
    public BloqueoAgenda bloquear(@RequestBody BloquearHorarioDTO dto) {
        return bloqueoAgendaService.bloquearHorario(dto);
    }
}