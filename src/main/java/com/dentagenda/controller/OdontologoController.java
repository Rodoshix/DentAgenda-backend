package com.dentagenda.controller;

import com.dentagenda.dto.RegistroOdontologoDTO;
import com.dentagenda.model.Odontologo;
import com.dentagenda.service.OdontologoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/odontologos")
public class OdontologoController {

    @Autowired
    private OdontologoService odontologoService;

    @PostMapping("/registro")
    public ResponseEntity<Odontologo> registrar(@Valid @RequestBody RegistroOdontologoDTO dto) {
        return ResponseEntity.ok(odontologoService.registrarOdontologo(dto));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarOdontologo(@PathVariable Long id) {
        odontologoService.eliminarOdontologo(id);
        return ResponseEntity.noContent().build();
    }
}