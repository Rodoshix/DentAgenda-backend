package com.dentagenda.service.impl;

import com.dentagenda.model.Usuario;
import com.dentagenda.dto.CrearUsuarioDTO;
import com.dentagenda.model.RolUsuario;
import com.dentagenda.repository.UsuarioRepository;
import com.dentagenda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario crearUsuario(CrearUsuarioDTO dto) {
        if (usuarioRepository.findByRut(dto.getRut()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(dto.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido. Debe ser ODONTOLOGO o RECEPCIONISTA.");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .rut(dto.getRut())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(rol)
                .build();

        return usuarioRepository.save(nuevoUsuario);
    }
}