package com.dentagenda.service.impl;

import com.dentagenda.dto.RegistroOdontologoDTO;
import com.dentagenda.model.Odontologo;
import com.dentagenda.model.RolUsuario;
import com.dentagenda.model.Usuario;
import com.dentagenda.repository.OdontologoRepository;
import com.dentagenda.repository.UsuarioRepository;
import com.dentagenda.service.OdontologoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class OdontologoServiceImpl implements OdontologoService {

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public Odontologo registrarOdontologo(RegistroOdontologoDTO dto) {
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setRut(dto.getRut());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(RolUsuario.ODONTOLOGO);

        // Crear odontólogo y asociar el usuario
        Odontologo odontologo = new Odontologo();
        odontologo.setNombre(dto.getNombre());
        odontologo.setRut(dto.getRut());
        odontologo.setCorreo(dto.getCorreo());
        odontologo.setEspecialidad(dto.getEspecialidad());
        odontologo.setUsuario(usuario); // aquí está la magia

        return odontologoRepository.save(odontologo);
    }

    @Override
    public void eliminarOdontologo(Long id) {
        Odontologo odontologo = odontologoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        odontologoRepository.delete(odontologo);

        if (odontologo.getUsuario() != null) {
            usuarioRepository.delete(odontologo.getUsuario());
        }
    }
}