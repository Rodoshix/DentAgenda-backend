package com.dentagenda.service;

import com.dentagenda.dto.RegistroRecepcionistaDTO;
import com.dentagenda.model.Recepcionista;

import java.util.List;

public interface RecepcionistaService {
    Recepcionista registrarRecepcionista(RegistroRecepcionistaDTO dto);
    void eliminarRecepcionista(Long id);
    List<Recepcionista> listarTodos();
}