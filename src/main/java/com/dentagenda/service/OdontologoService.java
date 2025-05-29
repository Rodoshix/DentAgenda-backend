package com.dentagenda.service;

import com.dentagenda.dto.RegistroOdontologoDTO;
import com.dentagenda.model.Odontologo;

public interface OdontologoService {
    Odontologo registrar(RegistroOdontologoDTO dto);
}