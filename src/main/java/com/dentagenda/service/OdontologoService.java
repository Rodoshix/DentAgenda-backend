package com.dentagenda.service;

import com.dentagenda.dto.RegistroOdontologoDTO;
import com.dentagenda.model.Odontologo;

import java.util.List;

public interface OdontologoService {
    Odontologo registrarOdontologo(RegistroOdontologoDTO dto);
    void eliminarOdontologo(Long id);
    List<Odontologo> listarTodos();
}