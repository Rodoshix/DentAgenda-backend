package com.dentagenda.service.impl;

import com.dentagenda.dto.RegistroOdontologoDTO;
import com.dentagenda.model.Odontologo;
import com.dentagenda.repository.OdontologoRepository;
import com.dentagenda.service.OdontologoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OdontologoServiceImpl implements OdontologoService {

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Override
    public Odontologo registrar(RegistroOdontologoDTO dto) {
        if (odontologoRepository.findByRut(dto.getRut()).isPresent()) {
            throw new RuntimeException("Ya existe un odontólogo con este RUT.");
        }

        Odontologo odontologo = new Odontologo();
        odontologo.setNombre(dto.getNombre());
        odontologo.setRut(dto.getRut());
        odontologo.setCorreo(dto.getCorreo());
        odontologo.setEspecialidad(dto.getEspecialidad());

        return odontologoRepository.save(odontologo);
    }
}