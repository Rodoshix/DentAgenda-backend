package com.dentagenda.service.impl;

import com.dentagenda.dto.RegistroRecepcionistaDTO;
import com.dentagenda.model.Recepcionista;
import com.dentagenda.model.Usuario;
import com.dentagenda.model.RolUsuario;
import com.dentagenda.repository.RecepcionistaRepository;
import com.dentagenda.repository.UsuarioRepository;
import com.dentagenda.service.RecepcionistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecepcionistaServiceImpl implements RecepcionistaService {

    @Autowired
    private RecepcionistaRepository recepcionistaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Recepcionista registrarRecepcionista(RegistroRecepcionistaDTO dto) {
        if (usuarioRepository.findByRut(dto.getRut()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con este RUT.");
        }

        Usuario usuario = Usuario.builder()
                .rut(dto.getRut())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(RolUsuario.RECEPCIONISTA)
                .build();
        usuarioRepository.save(usuario);

        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setNombre(dto.getNombre());
        recepcionista.setRut(dto.getRut());
        recepcionista.setCorreo(dto.getCorreo());
        recepcionista.setTelefono(dto.getTelefono());
        recepcionista.setUsuario(usuario);

        return recepcionistaRepository.save(recepcionista);
    }
}