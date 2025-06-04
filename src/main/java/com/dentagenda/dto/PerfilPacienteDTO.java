package com.dentagenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerfilPacienteDTO {
    private String rut;
    private String nombre;
    private String correo;
    private String telefono;
    private String rol;
}