package com.dentagenda.controller;

import com.dentagenda.dto.RegistroRecepcionistaDTO;
import com.dentagenda.model.Recepcionista;
import com.dentagenda.service.RecepcionistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recepcionistas")
public class RecepcionistaController {

    @Autowired
    private RecepcionistaService recepcionistaService;

    @PostMapping("/registro")
    public ResponseEntity<Recepcionista> registrar(@Valid @RequestBody RegistroRecepcionistaDTO dto) {
        Recepcionista nuevo = recepcionistaService.registrarRecepcionista(dto);
        return ResponseEntity.ok(nuevo);
    }
}