package com.dentagenda.security;

import com.dentagenda.model.Usuario;
import com.dentagenda.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String rut) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByRut(rut)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con rut: " + rut));

        return new User(
            usuario.getRut(),
            usuario.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
}