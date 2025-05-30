package com.dentagenda.service;

import com.dentagenda.model.Usuario;
import com.dentagenda.model.RolUsuario;

public interface UsuarioService {
    Usuario crearUsuario(String rut, String password, RolUsuario rol);
}