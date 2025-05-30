package com.dentagenda.service;

import com.dentagenda.dto.RegistroOdontologoDTO;
import com.dentagenda.model.Odontologo;

public interface OdontologoService {
    Odontologo registrarOdontologo(RegistroOdontologoDTO dto);
    void eliminarOdontologo(Long id);
}