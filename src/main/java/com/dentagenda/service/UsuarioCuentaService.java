package com.dentagenda.service;

import com.dentagenda.dto.CambiarPasswordDTO;
import com.dentagenda.dto.EditarPerfilDTO;
import com.dentagenda.dto.RecuperarPasswordDTO;
import com.dentagenda.dto.RestablecerPasswordDTO;
import com.dentagenda.model.Usuario;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioCuentaService {
    Usuario obtenerPerfil(UserDetails userDetails);
    void cambiarPassword(UserDetails userDetails, CambiarPasswordDTO dto);
    void enviarRecuperacion(RecuperarPasswordDTO dto);
    void restablecerPassword(RestablecerPasswordDTO dto);
    void editarPerfil(String rut, EditarPerfilDTO dto);
}