package com.dentagenda.service.impl;

import com.dentagenda.model.Usuario;
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
    public Usuario crearUsuario(String rut, String password, RolUsuario rol) {
        if (usuarioRepository.findByRut(rut).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .rut(rut)
                .password(passwordEncoder.encode(password))
                .rol(rol)
                .build();

        return usuarioRepository.save(nuevoUsuario);
    }
}