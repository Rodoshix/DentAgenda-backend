package com.dentagenda.controller;

import com.dentagenda.model.Usuario;
import com.dentagenda.model.RolUsuario;
import com.dentagenda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuario(
            @RequestParam String rut,
            @RequestParam String password,
            @RequestParam RolUsuario rol
    ) {
        Usuario creado = usuarioService.crearUsuario(rut, password, rol);
        return ResponseEntity.ok(creado);
    }
}