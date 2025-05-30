package com.dentagenda.service;

import com.dentagenda.model.Usuario;
import com.dentagenda.dto.CrearUsuarioDTO;


public interface UsuarioService {
    Usuario crearUsuario(CrearUsuarioDTO dto);
}